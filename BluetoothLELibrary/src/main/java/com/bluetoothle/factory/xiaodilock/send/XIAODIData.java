package com.bluetoothle.factory.xiaodilock.send;

/**
 * 数据中心集合
 */
public class XIAODIData {
	private byte[] channelpwd;		//信道密码
	private byte[] newchannelpwd;	//新信道密码
	private byte[] mobileaccount;	//手机账号
	private byte[] openlocktype;	//开锁方式
	private byte[] time;			//时间
	private byte[] fingerpageid;	//指纹ID
	private byte[] smartkeyid;		//智能钥匙ID
	private byte[] lockname;		//锁名称
	private byte[] openlockpwd;		//开锁密码
	private byte[] closelockpwd;	//关闭开锁密码
	private byte[] managepwd;		//管理密码
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
	public XIAODIData setChannelpwd(byte[] channelpwd) {
		this.channelpwd = channelpwd;
		return this;
	}
	public byte[] getNewchannelpwd() {
		return newchannelpwd;
	}
	public XIAODIData setNewchannelpwd(byte[] newchannelpwd) {
		this.newchannelpwd = newchannelpwd;
		return this;
	}
	public byte[] getMobileaccount() {
		return mobileaccount;
	}
	public XIAODIData setMobileaccount(byte[] mobileaccount) {
		this.mobileaccount = mobileaccount;
		return this;
	}
	public byte[] getOpenlocktype() {
		return openlocktype;
	}
	public XIAODIData setOpenlocktype(byte[] openlocktype) {
		this.openlocktype = openlocktype;
		return this;
	}
	public byte[] getTime() {
		return time;
	}
	public XIAODIData setTime(byte[] time) {
		this.time = time;
		return this;
	}
	public byte[] getFingerpageid() {
		return fingerpageid;
	}
	public XIAODIData setFingerpageid(byte[] fingerpageid) {
		this.fingerpageid = fingerpageid;
		return this;
	}
	public byte[] getSmartkeyid() {
		return smartkeyid;
	}
	public XIAODIData setSmartkeyid(byte[] smartkeyid) {
		this.smartkeyid = smartkeyid;
		return this;
	}
	public byte[] getLockname() {
		return lockname;
	}
	public XIAODIData setLockname(byte[] lockname) {
		this.lockname = lockname;
		return this;
	}
	public byte[] getOpenlockpwd() {
		return openlockpwd;
	}
	public XIAODIData setOpenlockpwd(byte[] openlockpwd) {
		this.openlockpwd = openlockpwd;
		return this;
	}
	public byte[] getManagepwd() {
		return managepwd;
	}
	public XIAODIData setManagepwd(byte[] managepwd) {
		this.managepwd = managepwd;
		return this;
	}
	public byte[] getLovealarmflag() {
		return lovealarmflag;
	}
	public XIAODIData setLovealarmflag(byte[] lovealarmflag) {
		this.lovealarmflag = lovealarmflag;
		return this;
	}
	public byte[] getTimestatus() {
		return timestatus;
	}
	public XIAODIData setTimestatus(byte[] timestatus) {
		this.timestatus = timestatus;
		return this;
	}
	public byte[] getTimerange() {
		return timerange;
	}
	public XIAODIData setTimerange(byte[] timerange) {
		this.timerange = timerange;
		return this;
	}
	public byte[] getWifissid() {
		return wifissid;
	}
	public XIAODIData setWifissid(byte[] wifissid) {
		this.wifissid = wifissid;
		return this;
	}
	public byte[] getWifipassword() {
		return wifipassword;
	}
	public XIAODIData setWifipassword(byte[] wifipassword) {
		this.wifipassword = wifipassword;
		return this;
	}
	public byte[] getAlarmpwd() {
		return alarmpwd;
	}
	public XIAODIData setAlarmpwd(byte[] alarmpwd) {
		this.alarmpwd = alarmpwd;
		return this;
	}
	public byte[] getSecretkey() {
		return secretkey;
	}
	public XIAODIData setSecretkey(byte[] secretkey) {
		this.secretkey = secretkey;
		return this;
	}
	public byte[] getOpenlogtoggle() {
		return openlogtoggle;
	}
	public XIAODIData setOpenlogtoggle(byte[] openlogtoggle) {
		this.openlogtoggle = openlogtoggle;
		return this;
	}
	public byte[] getCloselockpwd() {
		return closelockpwd;
	}
	public XIAODIData setCloselockpwd(byte[] closelockpwd) {
		this.closelockpwd = closelockpwd;
		return this;
	}
	public byte[] getSecretkey13() {
		return secretkey13;
	}
	public XIAODIData setSecretkey13(byte[] secretkey13) {
		this.secretkey13 = secretkey13;
		return this;
	}
	public byte[] getLockmac() {
		return lockmac;
	}
	public XIAODIData setLockmac(byte[] lockmac) {
		this.lockmac = lockmac;
		return this;
	}

	public boolean getEnbleWifi() {
		return enbleWifi;
	}

	public XIAODIData setEnbleWifi(boolean enbleWifi) {
		this.enbleWifi = enbleWifi;
		return this;
	}
}
