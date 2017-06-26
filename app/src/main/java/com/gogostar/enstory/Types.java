package com.gogostar.enstory;

/**
 * Created by Administrator on 2017/2/15.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 充值记录信息
 */
class RechargeInfo {

	public String date;
	public String money;
	public int number;

	public RechargeInfo(String date, String money, int number) {
		this.date = date;
		this.money = money;
		this.number = number;
	}

	public String getDate() {
		return date;
	}

	public String getMoney() {
		return money;
	}

	public int getNumber() {
		return number;
	}
}

/**
 * 消费记录信息
 */
class ConsumptionInfo {

	public String date;
	public String money;
	public String thing;
	public String price;

	public ConsumptionInfo(String date, String thing, String price, String money) {
		this.date = date;
		this.money = money;
		this.thing = thing;
		this.price = price;
	}

	public String getDate() {
		return date;
	}

	public String getMoney() {
		return money;
	}

	public String getThing() {
		return thing;
	}

	public String getPrice() {
		return price;
	}
}

class Profiles {
	private String name;
	private int id;

	public Profiles(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getImageId() {
		return id;
	}
}

/**
 * 用户信息
 */
class UserInfo implements Serializable {
	int uId;
	String uAccount;
	String uPassword;
	String uName;
	String uTel;
	byte[] uHeader;
	int uLevel;
	int uCoin;
	String uNote;
	boolean uValid;
	String uRegisterTime;
	String uLastAccess;
	String uContact;

	public void setuId(int id) {
		this.uId = id;
	}

	public int getuId() {
		return uId;
	}

	public void setuAccount(String account) {
		this.uAccount = account;
	}

	public String getuAccount() {
		return uAccount;
	}

	public void setuPassword(String password) {
		this.uPassword = password;
	}

	public String getuPassword() {
		return uPassword;
	}

	public void setuName(String name) {
		this.uName = name;
	}

	public String getuName() {
		return uName;
	}

	public void setuTel(String tel) {
		this.uTel = tel;
	}

	public String getuTel() {
		return uTel;
	}

	public void setuLevel(int level) {
		this.uLevel = level;
	}

	public int getuLevel() {
		return uLevel;
	}

	public void setuCoin(int coin) {
		this.uCoin = coin;
	}

	public int getuCoin() {
		return uCoin;
	}

	public void setuNote(String note) {
		this.uNote = note;
	}

	public String getuNote() {
		return uNote;
	}

	public void setuValid(boolean valid) {
		this.uValid = valid;
	}

	public boolean getuValid() {
		return uValid;
	}

	public void setuRegisterTime(String registerTime) {
		this.uRegisterTime = registerTime;
	}

	public String getuRegisterTime() {
		return uRegisterTime;
	}

	public void setuLastAccess(String lastAccess) {
		this.uLastAccess = lastAccess;
	}

	public String getuLastAccess() {
		return uLastAccess;
	}

	public void setuHeader(byte[] header) {
		this.uHeader = header;
	}

	public byte[] getuHeader() {
		return uHeader;
	}

	public void setuContact(String contact) {
		this.uContact = contact;
	}

	public String getuContact() {
		return uContact;
	}
}

/**
 * 绘本信息
 */
class PictureInfo implements Parcelable, Serializable {

	// 视频id
	private int id;
	private int category_id;
	private int order;
	// 视频名字
	private String title;
	// 视频路径
	private String video_path;
	// 视频展示配置表封面图URL
	private String video_img;
	private float price;
	private String video_src;
	private byte[] bitmap;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	public void setVideo_img(String video_img) {
		this.video_img = video_img;
	}

	public String getVideo_img() {
		return video_img;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setVideo_path(String video_path) {
		this.video_path = video_path;
	}

	public String getVideo_path() {
		return video_path;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getPrice() {
		return price;
	}

	public void setVideo_src(String video_src) {
		this.video_src = video_src;
	}

	public String getVideo_src() {
		return video_src;
	}

	public void setBitmap(byte[] bitmap) {
		this.bitmap = bitmap;
	}

	public byte[] getBitmap() {
		return bitmap;
	}

	public static final Parcelable.Creator<PictureInfo> CREATOR = new Creator<PictureInfo>() {
		@Override
		public PictureInfo createFromParcel(Parcel source) {
			PictureInfo pictureInfo = new PictureInfo();

			pictureInfo.id = source.readInt();
			pictureInfo.category_id = source.readInt();
			pictureInfo.order = source.readInt();
			pictureInfo.title = source.readString();
			pictureInfo.video_path = source.readString();
			pictureInfo.video_img = source.readString();
			pictureInfo.price = source.readFloat();
			pictureInfo.video_src = source.readString();
			pictureInfo.bitmap = source.createByteArray();

			return pictureInfo;
		}

		@Override
		public PictureInfo[] newArray(int size) {
			return new PictureInfo[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(category_id);
		dest.writeInt(order);
		dest.writeString(title);
		dest.writeString(video_path);
		dest.writeString(video_img);
		dest.writeFloat(price);
		dest.writeString(video_src);
		dest.writeByteArray(bitmap);
	}
}

class CategoryInfo {

	private int id;
	private String name;
	private String description;

	public CategoryInfo(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}

