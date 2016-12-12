/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\Java\\Android\\clone\\KuaiHu-master\\asyncHttp\\src\\main\\aidl\\tuyaxiong\\asynchttp\\BookManager.aidl
 */
package tuyaxiong.asynchttp;
// BookManager.aidl
//第二类AIDL文件
//作用是定义方法接口
//导入所需要使用的非默认支持数据类型的包

public interface BookManager extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements tuyaxiong.asynchttp.BookManager
{
private static final java.lang.String DESCRIPTOR = "tuyaxiong.asynchttp.BookManager";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an tuyaxiong.asynchttp.BookManager interface,
 * generating a proxy if needed.
 */
public static tuyaxiong.asynchttp.BookManager asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof tuyaxiong.asynchttp.BookManager))) {
return ((tuyaxiong.asynchttp.BookManager)iin);
}
return new tuyaxiong.asynchttp.BookManager.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getBooks:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<tuyaxiong.asynchttp.Book> _result = this.getBooks();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_addBook:
{
data.enforceInterface(DESCRIPTOR);
tuyaxiong.asynchttp.Book _arg0;
if ((0!=data.readInt())) {
_arg0 = tuyaxiong.asynchttp.Book.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.addBook(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements tuyaxiong.asynchttp.BookManager
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     *///所有的返回值前都不需要加任何东西，不管是什么数据类型

@Override public java.util.List<tuyaxiong.asynchttp.Book> getBooks() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<tuyaxiong.asynchttp.Book> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBooks, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(tuyaxiong.asynchttp.Book.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//传参时除了Java基本类型以及String，CharSequence之外的类型
//都需要在前面加上定向tag，具体加什么量需而定

@Override public void addBook(tuyaxiong.asynchttp.Book book) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((book!=null)) {
_data.writeInt(1);
book.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getBooks = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_addBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
/**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     *///所有的返回值前都不需要加任何东西，不管是什么数据类型

public java.util.List<tuyaxiong.asynchttp.Book> getBooks() throws android.os.RemoteException;
//传参时除了Java基本类型以及String，CharSequence之外的类型
//都需要在前面加上定向tag，具体加什么量需而定

public void addBook(tuyaxiong.asynchttp.Book book) throws android.os.RemoteException;
}
