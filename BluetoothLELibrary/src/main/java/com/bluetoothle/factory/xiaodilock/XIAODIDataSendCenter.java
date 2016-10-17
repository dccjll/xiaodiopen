package com.bluetoothle.factory.xiaodilock;


/**
 * 数据中心集合
 * @author dessmann
 *
 */
public class XIAODIDataSendCenter {
	private byte[] channelpwd;		//信道密码
	private byte[] newchannelpwd;	//新信道密码
	private byte[] mobileaccount;	//手机账号
	private byte[] openlocktype;	//开锁方式
	private byte[] time;			//时间
	private String fingerpageid;	//指纹ID
	private byte[] smartkeyid;		//智能钥匙ID
	private String lockname;		//锁名称
	private String openlockpwd;		//开锁密码
	private byte[] closelockpwd;	//关闭开锁密码
	private String managepwd;		//管理密码
	private byte[] lovealarmflag;	//亲情报警标志
	private byte[] timestatus;		//时效状态
	private byte[] timerange;		//时效范围
	private byte[] wifissid;		//wifi ssid
	private byte[] wifipassword;	//wifi密码	
	private byte[] alarmpwd;		//报警密码
	private byte[] secretkey;		//秘钥
	private byte[] openlogtoggle;	//开锁日志开关
	private byte[] secretkey13;		//自己生成的13个字节秘钥
	private byte[] lockmac;			//mac地址
	private boolean enbleWifi;		//是否开启WIFI
	public byte[] getChannelpwd() {
		return channelpwd;
	}
	public XIAODIDataSendCenter setChannelpwd(byte[] channelpwd) {
		this.channelpwd = channelpwd;
		return this;
	}
	public byte[] getNewchannelpwd() {
		return newchannelpwd;
	}
	public XIAODIDataSendCenter setNewchannelpwd(byte[] newchannelpwd) {
		this.newchannelpwd = newchannelpwd;
		return this;
	}
	public byte[] getMobileaccount() {
		return mobileaccount;
	}
	public XIAODIDataSendCenter setMobileaccount(byte[] mobileaccount) {
		this.mobileaccount = mobileaccount;
		return this;
	}
	public byte[] getOpenlocktype() {
		return openlocktype;
	}
	public XIAODIDataSendCenter setOpenlocktype(byte[] openlocktype) {
		this.openlocktype = openlocktype;
		return this;
	}
	public byte[] getTime() {
		return time;
	}
	public XIAODIDataSendCenter setTime(byte[] time) {
		this.time = time;
		return this;
	}
	public String getFingerpageid() {
		return fingerpageid;
	}
	public XIAODIDataSendCenter setFingerpageid(String fingerpageid) {
		this.fingerpageid = fingerpageid;
		return this;
	}
	public byte[] getSmartkeyid() {
		return smartkeyid;
	}
	public XIAODIDataSendCenter setSmartkeyid(byte[] smartkeyid) {
		this.smartkeyid = smartkeyid;
		return this;
	}
	public String getLockname() {
		return lockname;
	}
	public XIAODIDataSendCenter setLockname(String lockname) {
		this.lockname = lockname;
		return this;
	}
	public String getOpenlockpwd() {
		return openlockpwd;
	}
	public XIAODIDataSendCenter setOpenlockpwd(String openlockpwd) {
		this.openlockpwd = openlockpwd;
		return this;
	}
	public String getManagepwd() {
		return managepwd;
	}
	public XIAODIDataSendCenter setManagepwd(String managepwd) {
		this.managepwd = managepwd;
		return this;
	}
	public byte[] getLovealarmflag() {
		return lovealarmflag;
	}
	public XIAODIDataSendCenter setLovealarmflag(byte[] lovealarmflag) {
		this.lovealarmflag = lovealarmflag;
		return this;
	}
	public byte[] getTimestatus() {
		return timestatus;
	}
	public XIAODIDataSendCenter setTimestatus(byte[] timestatus) {
		this.timestatus = timestatus;
		return this;
	}
	public byte[] getTimerange() {
		return timerange;
	}
	public XIAODIDataSendCenter setTimerange(byte[] timerange) {
		this.timerange = timerange;
		return this;
	}
	public byte[] getWifissid() {
		return wifissid;
	}
	public XIAODIDataSendCenter setWifissid(byte[] wifissid) {
		this.wifissid = wifissid;
		return this;
	}
	public byte[] getWifipassword() {
		return wifipassword;
	}
	public XIAODIDataSendCenter setWifipassword(byte[] wifipassword) {
		this.wifipassword = wifipassword;
		return this;
	}
	public byte[] getAlarmpwd() {
		return alarmpwd;
	}
	public XIAODIDataSendCenter setAlarmpwd(byte[] alarmpwd) {
		this.alarmpwd = alarmpwd;
		return this;
	}
	public byte[] getSecretkey() {
		return secretkey;
	}
	public XIAODIDataSendCenter setSecretkey(byte[] secretkey) {
		this.secretkey = secretkey;
		return this;
	}
	public byte[] getOpenlogtoggle() {
		return openlogtoggle;
	}
	public XIAODIDataSendCenter setOpenlogtoggle(byte[] openlogtoggle) {
		this.openlogtoggle = openlogtoggle;
		return this;
	}
	public byte[] getCloselockpwd() {
		return closelockpwd;
	}
	public XIAODIDataSendCenter setCloselockpwd(byte[] closelockpwd) {
		this.closelockpwd = closelockpwd;
		return this;
	}
	public byte[] getSecretkey13() {
		return secretkey13;
	}
	public XIAODIDataSendCenter setSecretkey13(byte[] secretkey13) {
		this.secretkey13 = secretkey13;
		return this;
	}
	public byte[] getLockmac() {
		return lockmac;
	}
	public XIAODIDataSendCenter setLockmac(byte[] lockmac) {
		this.lockmac = lockmac;
		return this;
	}

	public boolean getEnbleWifi() {
		return enbleWifi;
	}

	public XIAODIDataSendCenter setEnbleWifi(boolean enbleWifi) {
		this.enbleWifi = enbleWifi;
		return this;
	}
}
