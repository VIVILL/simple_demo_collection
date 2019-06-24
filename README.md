# simple_demo_collection
一些简单的demo合集。以下为简单说明。

1.AndroidTcpClient

用于控制网络继电器设备。运行环境：Android5.

2.WebView

WebView的简单使用。运行环境：Android7.

3.FullScreenTest

界面全屏,运行环境：Android7.

4.EthTest

android获取以太网ip并执行ping ip地址命令的简单demo.

参考链接：

android7.0获取静态IP、网关、子网掩码、DNS
https://blog.csdn.net/dami_lixm/article/details/86533906

android 获取以太网的动态IP地址，子网掩码，DNS地址，网关地址
https://blog.csdn.net/sinat_38892960/article/details/86999772

Android通过ping操作进行网络检测，并返回花费的时间
[https://blog.csdn.net/li13650639161/article/details/78465850](https://blog.csdn.net/li13650639161/article/details/78465850)

Android中通过xml给布局添加边框
[https://blog.csdn.net/honey_angle_first/article/details/77323286](https://blog.csdn.net/honey_angle_first/article/details/77323286)

5.reboot  

根据AlarmDemo改了一点，功能为定时重启。运行环境：Android4.4. android 6（需系统签名）。

参考链接：

AlarmDemo

https://github.com/muxiaofufeng/AlarmDemo

6.USBSerialDemo

USBSerialDemo是在usb-serial-for-android 项目的基础上修改而成。

链接：https://github.com/mik3y/usb-serial-for-android

使用说明

1.在device_filter.xml中添加外接USB设备的pid、uid。
例如：
<usb-device vendor-id="1a86" product-id="7523" />

2.在打开串口设备之后，设置波特率（usb与串口通讯需匹配波特率）。

例如我的USB设备是9600就把代码里面的波特率改为9600.

![](https://i.imgur.com/A2Ej7C1.png)



    try {
      sPort.open(connection);
    //修改处
    // sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
      sPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
    }
    

3.定义打开usb和关闭usb的命令（不同的设备命令可能不一样），按需求发送命令。

例如我手上的USB设备定义了2个命令，打开关闭。


![](https://i.imgur.com/wGT5wna.png)




//打开USB


    byte[] openUsb = new byte[]{(byte) 0xA0,(byte) 0x01, (byte)0x01,(byte)0xA2};

//关闭USB


    byte[] closeUsb = new byte[]{(byte) 0xA0,(byte) 0x01, (byte)0x00,(byte)0xA1};

发送命令调用UsbSerialPort.write即可。

例如：
sPort.write(openUsb,1000);
sPort.write(closeUsb,1000);



