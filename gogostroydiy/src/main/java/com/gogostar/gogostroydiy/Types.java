package com.gogostar.gogostroydiy;

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
	String uHeader;
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

	public void setuHeader(String header) {
		this.uHeader = header;
	}

	public String getuHeader() {
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

	// 绘本id
	private int id;
	private int category_id;
	private int order;
	// 绘本名字
	private String name;
	// 绘本路径
	private String picture_path;
	// 绘本展示配置表封面图URL
	private String picture_img;
	private float price;
	private String picture_src;
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

	public void setPicture_img(String video_img) {
		this.picture_img = video_img;
	}

	public String getPicture_img() {
		return picture_img;
	}

	public void setTitle(String title) {
		this.name = title;
	}

	public String getTitle() {
		return name;
	}

	public void setPicture_path(String video_path) {
		this.picture_path = video_path;
	}

	public String getPicture_path() {
		return picture_path;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getPrice() {
		return price;
	}

	public void setPicture_src(String video_src) {
		this.picture_src = video_src;
	}

	public String getPicture_src() {
		return picture_src;
	}

	public void setBitmap(byte[] bitmap) {
		this.bitmap = bitmap;
	}

	public byte[] getBitmap() {
		return bitmap;
	}

	public static final Creator<PictureInfo> CREATOR = new Creator<PictureInfo>() {
		@Override
		public PictureInfo createFromParcel(Parcel source) {
			PictureInfo pictureInfo = new PictureInfo();

			pictureInfo.id = source.readInt();
			pictureInfo.category_id = source.readInt();
			pictureInfo.order = source.readInt();
			pictureInfo.name = source.readString();
			pictureInfo.picture_path = source.readString();
			pictureInfo.picture_img = source.readString();
			pictureInfo.price = source.readFloat();
			pictureInfo.picture_src = source.readString();
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
		dest.writeString(name);
		dest.writeString(picture_path);
		dest.writeString(picture_img);
		dest.writeFloat(price);
		dest.writeString(picture_src);
		dest.writeByteArray(bitmap);
	}
}

/**
 * 分类信息
 */
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

