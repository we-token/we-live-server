package show.we.service.eth;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.BooleanResponse;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.geth.Geth;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

public class EthService {
	private static String DEF_URL = "http://127.0.0.1:8545";
//	 private final static String DEF_URL =
//	 "https://rinkeby.infura.io/kF3ENaQiBKW3uQlgIWL3";
	private static HttpService httpService;

	public EthService() {
		url = DEF_URL;
	}

	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 初始化web3j普通api调用
	 *
	 * @return web3j
	 */
	public Web3j initWeb3j() {
		return Web3j.build(getService());
	}

	/**
	 * 初始化personal级别的操作对象
	 * 
	 * @return Geth
	 */
	public Geth initGeth() {
		return Geth.build(getService());
	}

	/**
	 * 初始化admin级别操作的对象
	 * 
	 * @return Admin
	 */
	public Admin initAdmin() {
		return Admin.build(getService());
	}

	/**
	 * 通过http连接到geth节点
	 * 
	 * @return
	 */
	private HttpService getService() {
		if (httpService == null) {
			httpService = new HttpService(url);
		}
		return httpService;
	}

	/**
	 * 输入密码创建地址
	 *
	 * @param password
	 *            密码（建议同一个平台的地址使用一个相同的，且复杂度较高的密码）
	 * @return 地址hash
	 * @throws IOException
	 */
	public String newAccount(Admin admin, String password) throws IOException {
		Request<?, NewAccountIdentifier> request = admin.personalNewAccount(password);
		NewAccountIdentifier result = request.send();
		return result.getAccountId();
	}

	/**
	 * 获得当前区块高度
	 *
	 * @return 当前区块高度
	 * @throws IOException
	 */
	public static BigInteger getCurrentBlockNumber(Web3j web3j) throws IOException {
		Request<?, EthBlockNumber> request = web3j.ethBlockNumber();
		return request.send().getBlockNumber();
	}

	/**
	 * 解锁账户，发送交易前需要对账户进行解锁
	 *
	 * @param address
	 *            地址
	 * @param password
	 *            密码
	 * @param duration
	 *            解锁有效时间，单位秒
	 * @return
	 * @throws IOException
	 */
	public static Boolean unlockAccount(Admin admin, String address, String password, BigInteger duration)
			throws IOException {
		Request<?, PersonalUnlockAccount> request = admin.personalUnlockAccount(address, password, duration);
		PersonalUnlockAccount account = request.send();
		return account.accountUnlocked();
	}

	/**
	 * 账户解锁，使用完成之后需要锁定
	 *
	 * @param address
	 * @return
	 * @throws IOException
	 */
	public static Boolean lockAccount(Geth geth, String address) throws IOException {
		Request<?, BooleanResponse> request = geth.personalLockAccount(address);
		BooleanResponse response = request.send();
		return response.success();
	}

	/**
	 * 根据hash值获取交易
	 *
	 * @param hash
	 * @return
	 * @throws IOException
	 */
	public static EthTransaction getTransactionByHash(Web3j web3j, String hash) throws IOException {
		Request<?, EthTransaction> request = web3j.ethGetTransactionByHash(hash);
		return request.send();
	}

	/**
	 * 获得ethblock
	 *
	 * @param blockNumber
	 *            根据区块编号
	 * @return
	 * @throws IOException
	 */
	public static EthBlock getBlockEthBlock(Web3j web3j, BigInteger blockNumber) throws IOException {
		DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(blockNumber);
		Request<?, EthBlock> request = web3j.ethGetBlockByNumber(defaultBlockParameter, true);
		EthBlock ethBlock = request.send();
		return ethBlock;
	}

	/**
	 * 发送交易并获得交易hash值
	 *
	 * @param transaction
	 * @param password
	 * @return
	 * @throws IOException
	 */
	public static String sendTransaction(Admin admin, Transaction transaction, String password) throws IOException {
		Request<?, EthSendTransaction> request = admin.personalSendTransaction(transaction, password);
		EthSendTransaction ethSendTransaction = request.send();
		return ethSendTransaction.getTransactionHash();
	}

	/**
	 * 
	 * @param web3j
	 * @param credentials
	 * @param toAddr
	 * @param value
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws TransactionException
	 * @throws Exception
	 */
	public static String sendTransaction(Web3j web3j, Credentials credentials, String toAddr, BigDecimal value,
			Convert.Unit unit) throws InterruptedException, IOException, TransactionException, Exception {
		TransactionReceipt tr = Transfer.sendFunds(web3j, credentials, toAddr, value, unit).send();
		return tr.getBlockHash();
	}

	/**
	 * 指定地址发送交易所需nonce获取
	 *
	 * @param address
	 *            待发送交易地址
	 * @return
	 * @throws IOException
	 */
	public static BigInteger getNonce(Web3j web3j, String address) throws IOException {
		Request<?, EthGetTransactionCount> request = web3j.ethGetTransactionCount(address,
				DefaultBlockParameterName.LATEST);
		return request.send().getTransactionCount();
	}
	
	
	public static BigInteger getBalance(Web3j web3j,String addr) throws IOException {
		return web3j.ethGetBalance(addr, DefaultBlockParameterName.LATEST).send().getBalance();
	}


	/**
	 * 倒入一个账户
	 * @param geth 
	 * @param credentials
	 * @param pwd
	 * @return
	 * @throws IOException
	 */
	public static String importCredentials(Geth geth, Credentials credentials, String pwd) throws IOException {
		return geth.personalImportRawKey(Numeric.toHexStringNoPrefix(credentials.getEcKeyPair().getPrivateKey()), pwd)
				.send().getResult();
	}
	
	public static String getAddrByPriKey(String pri) {
		if(Numeric.containsHexPrefix(pri)) {
			pri = Numeric.cleanHexPrefix(pri);
		}
		return Credentials.create(pri).getAddress();
	}
	
	
}
