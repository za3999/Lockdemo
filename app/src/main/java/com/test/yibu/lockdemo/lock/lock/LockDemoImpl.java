package com.test.yibu.lockdemo.lock.lock;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.util.Log;

import com.test.yibu.lockdemo.bean.LockMessage;
import com.test.yibu.lockdemo.lock.bluetooth.BluetoothConstant;
import com.test.yibu.lockdemo.lock.bluetooth.BluetoothLeHelper;
import com.test.yibu.lockdemo.lock.lock.linister.LockCloseListener;
import com.test.yibu.lockdemo.lock.lock.linister.OpenLockListener;
import com.test.yibu.lockdemo.util.LogHelper;

import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by zhengcf on 2017/11/20.
 */

public class LockDemoImpl implements ILock {

    private static final String TAG = "LockDemoImpl";

    private static final int OPTION_FREE = 0;
    private static final int OPTION_GET_TOKEN = 1;
    private static final int OPTION_GET_STATUS = 2;
    private static final int OPTION_OPEN_LOCK = 3;
    private static final UUID bltServerUUID = UUID.fromString("0000fee7-0000-1000-8000-00805f9b34fb");
    private static final UUID readDataUUID = UUID.fromString("000036f6-0000-1000-8000-00805f9b34fb");
    private static final UUID writeDataUUID = UUID.fromString("000036f5-0000-1000-8000-00805f9b34fb");


    private BluetoothLeHelper helper;
    private OpenLockListener openLockListener;
    private LockCloseListener lockCloseListener;


    private LockMessage lockMessage;
    private byte[] currSendData;// 当前发送指令
    private byte[] token; //每个蓝牙设备只需获取一次
    private static int customOption = 0;

    public LockDemoImpl(Context context) {
        helper = BluetoothLeHelper.getInstance(context);
    }

    @Override
    public boolean checkDevices(LockMessage message, BluetoothDevice device, byte[] scanRecord) {
        if (scanRecord[5] == 0x01 && scanRecord[6] == 0x02) {
            return true;
        }
        return false;
    }

    @Override
    public void openLock(LockMessage message, OpenLockListener openLockListener) {
        this.lockMessage = message;
        this.openLockListener = openLockListener;
        BluetoothLeHelper.GattAdapter openLockAdapter = new BluetoothLeHelper.GattAdapter() {

            @Override
            public void onDisconnected() {
                super.onDisconnected();
                if (helper.isBluetoothEnable()) {
                    helper.connect(lockMessage.getMac());
                }
            }

            @Override
            public void onServicesDiscovered() {
                super.onServicesDiscovered();
                helper.configCharacteristicNotification(bltServerUUID, readDataUUID, true);//配置监听状态
            }

            @Override
            public void onDescriptorWrite(BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(descriptor, status);
                sendGetTokenData();
            }

            @Override
            public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(characteristic);
                Log.w(TAG, "！！！onCharacteristicChanged！！！currOption:" + customOption + ", characteristic.getValue:::" + getStr4Byte(characteristic.getValue()));
                if (token == null) {
                    if (isSendDataSuccess(characteristic)) {
                        token = getToken(characteristic);
                        sendCheckStatusData();
                    } else {
                        sendGetTokenData();
                    }
                } else {
                    byte[] mingWen = getMingWen(characteristic);
                    if (isGetStatusCmdResult(mingWen)) {
                        if (isLockStatusOpen(mingWen)) {
                            openLockResult(false, LockConstant.LOCK_TYPE_ALREADY_OPEN);
                        } else {
                            sendOpenLockData();
                        }
                    } else if (isOpenLockCmdResult(mingWen)) {
                        openLockResult(isOpenLockSuccess(mingWen), LockConstant.LOCK_TYPE_SUCCESS);
                    } else {
                        isSendDataSuccess(characteristic);
                    }
                }
            }
        };
        this.openLockListener.setAdapter(openLockAdapter);
        helper.addGattAdapter(openLockAdapter);
        if (helper.getConnectionState() != BluetoothConstant.STATE_CONNECTED) {
            helper.connect(message.getMac());
        }
    }

    @Override
    public void monitoringLockClose(LockMessage message, LockCloseListener lockCloseListener) {
        this.lockMessage = message;
        this.lockCloseListener = lockCloseListener;
        BluetoothLeHelper.GattAdapter closeLockAdapter = new BluetoothLeHelper.GattAdapter() {

            @Override
            public void onDisconnected() {
                super.onDisconnected();
                if (helper.isBluetoothEnable()) {
                    helper.connect(lockMessage.getMac());
                }
            }

            @Override
            public void onServicesDiscovered() {
                super.onServicesDiscovered();
                helper.configCharacteristicNotification(bltServerUUID, readDataUUID, true);//配置监听状态
            }

            @Override
            public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(characteristic);
                if (notifyCloseLockSuccess(characteristic)) {
                    lockCloseResult(true);
                }
            }
        };
        this.lockCloseListener.setAdapter(closeLockAdapter);
        helper.addGattAdapter(closeLockAdapter);
        if (helper.getConnectionState() != BluetoothConstant.STATE_CONNECTED) {
            helper.connect(message.getMac());
        }
    }

    private void openLockResult(boolean isOpen, String message) {
        if (openLockListener != null) {
            helper.removeGattAdapter(openLockListener.getAdapter());
            if (!openLockListener.isTimeOut()) {
                openLockListener.onOpenLock(isOpen, message);
            }
            openLockListener = null;
        }
    }

    private void lockCloseResult(boolean isClose) {
        if (lockCloseListener != null) {
            helper.removeGattAdapter(lockCloseListener.getAdapter());
            lockCloseListener.onLockClose(isClose);
            lockCloseListener = null;
        }
    }


    /**
     * 发送获取令牌指令
     */
    private boolean sendGetTokenData() {
        LogHelper.w(TAG, "methord sendGetTokenData");
        boolean success = false;
        byte[] gettoken = {0x06, 0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        currSendData = gettoken;
        printBytes("cmd currSendData:", currSendData);
        customOption = OPTION_GET_TOKEN;
        success = sendCmd(gettoken);
        return success;
    }

    /**
     * 获取令牌指令
     *
     * @param characteristic
     * @return
     */
    private byte[] getToken(BluetoothGattCharacteristic characteristic) {
        LogHelper.w(TAG, "methord getToken");
        byte[] mingWen = getMingWen(characteristic);
        if (mingWen != null && mingWen.length == 16) {
            if (mingWen[0] == 0x06 && mingWen[1] == 0x02) {
                token = new byte[4];
                token[0] = mingWen[3];
                token[1] = mingWen[4];
                token[2] = mingWen[5];
                token[3] = mingWen[6];
            }
        }
        return token;
    }

    /**
     * 发送检查车辆状态
     */
    private boolean sendCheckStatusData() {
        boolean success = false;
        if (token != null) {
            byte[] checkStatus = {0x05, 0x0e, 0x01, 0x01, token[0], token[1], token[2], token[3], 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            currSendData = checkStatus;
            printBytes("cmd currSendData:", currSendData);
            customOption = OPTION_GET_STATUS;
            success = sendCmd(checkStatus);
        }
        LogHelper.w(TAG, "methord sendCheckStatusData:" + success);
        return success;
    }

    /**
     * 是否是获取锁状态的返回指令
     *
     * @param mingWen
     * @return
     */
    private boolean isGetStatusCmdResult(byte[] mingWen) {
        LogHelper.w(TAG, "methord isGetStatusCmdResult");
        return mingWen[0] == 0x05 && mingWen[1] == 0x0f && mingWen[2] == 0x01;
    }

    /**
     * 车辆状态
     *
     * @return
     */
    private boolean isLockStatusOpen(byte[] mingWen) {
        LogHelper.w(TAG, "methord isLockStatusOpen");
        return mingWen[3] == 0x00 ? true : false;
    }

    /**
     * 发送开锁指令
     */
    private boolean sendOpenLockData() {
        LogHelper.w(TAG, "methord sendOpenLockData");
        boolean success = false;
        if (token != null) {
            byte[] openLock = {0x05, 0x01, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, token[0], token[1], token[2], token[3], 0x00, 0x00, 0x00};
            System.arraycopy(lockMessage.getPasswordByte(), 0, openLock, 3, lockMessage.getPasswordByte().length); // 将密码复制到到命令
            currSendData = openLock;
            printBytes("cmd currSendData:", currSendData);
            customOption = OPTION_OPEN_LOCK;
            success = sendCmd(openLock);
        }
        return success;
    }


    /**
     * 是否是开锁的返回指令
     *
     * @param mingWen
     * @return
     */
    private boolean isOpenLockCmdResult(byte[] mingWen) {
        LogHelper.w(TAG, "methord isOpenLockCmdResult");
        return mingWen[0] == 0x05 && mingWen[1] == 0x02 && mingWen[2] == 0x01;
    }

    /**
     * 开锁是否成功
     *
     * @return
     */
    private boolean isOpenLockSuccess(byte[] mingWen) {
        LogHelper.w(TAG, "methord isOpenLockSuccess");
        return mingWen[3] == 0x00;
    }

    /**
     * 发送命令
     *
     * @param cmd
     * @return
     */
    private boolean sendCmd(byte[] cmd) {
        boolean success = false;
        byte miWen[] = Encrypt(cmd, lockMessage.getKeyByte());
        if (miWen != null) {
            customOption = OPTION_OPEN_LOCK;
            success = helper.writeCharacteristic(bltServerUUID, writeDataUUID, miWen);
            printBytes("cmd miwen:", miWen);
        }
        return success;
    }

    /**
     * 处理蓝牙终端主动返回的信息，例如关锁操作
     *
     * @param characteristic
     * @return
     */
    private boolean notifyCloseLockSuccess(BluetoothGattCharacteristic characteristic) {
        byte[] mingWen = getMingWen(characteristic);
        if (mingWen[0] == 0x05 && mingWen[1] == 0x08 && mingWen[2] == 0x01 && mingWen[3] == 0x00) {// 关锁成功
            return true;
        }
        return false;
    }

    /**
     * 判断蓝牙返回写命令成功与否
     *
     * @param characteristic
     * @return
     */
    private boolean isSendDataSuccess(BluetoothGattCharacteristic characteristic) {
        boolean success = false;
        byte[] mingWen = getMingWen(characteristic);
        if (mingWen[0] == (byte) 0xCB && mingWen[1] == currSendData[0] && mingWen[2] == currSendData[1]) {
            success = true;
        }
        LogHelper.w(TAG, "methord isSendDataSuccess:" + success);
        return success;
    }

    /**
     * 获取明文
     *
     * @param characteristic
     * @return
     */
    private byte[] getMingWen(BluetoothGattCharacteristic characteristic) {
        byte[] values = characteristic.getValue();
        byte[] x = new byte[16];
        System.arraycopy(values, 0, x, 0, 16);
        byte[] mingWen = Decrypt(x, lockMessage.getKeyByte());
        printBytes("cmd mingwen Data:", mingWen);
        return mingWen;
    }


    /**
     * 加密
     *
     * @param sSrc
     * @param sKey
     * @return
     */
    private byte[] Encrypt(byte[] sSrc, byte[] sKey) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(sKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");//"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc);
            return encrypted;//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 解密
     *
     * @param sSrc
     * @param sKey
     * @return
     */
    private byte[] Decrypt(byte[] sSrc, byte[] sKey) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(sKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] dncrypted = cipher.doFinal(sSrc);
            return dncrypted;
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getStr4Byte(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((int) b + ",");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    public static byte[] getByte4Str(String byteStr) {
        String[] byteStrArr = byteStr.split(",");
        byte[] bytes = new byte[byteStrArr.length];
        for (int i = 0; i < byteStrArr.length; i++) {
            bytes[i] = Byte.parseByte(byteStrArr[i]);
        }
        return bytes;
    }

    public static void printBytes(String header, byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((int) b + ",");
        }
        sb.delete(sb.length() - 1, sb.length());
        Log.e(TAG, header + sb);
    }
}
