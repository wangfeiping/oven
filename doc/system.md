### 系统安装  

### 下载系统镜像  
Arch:  
> https://archlinuxarm.org/platforms  
选择对应板卡系统版本 - [Raspberry Pi 3]  
> https://archlinuxarm.org/platforms/armv8/broadcom/raspberry-pi-3  
> http://os.archlinuxarm.org/os/ArchLinuxARM-rpi-3-latest.tar.gz  
  
Raspbian:  
> https://www.raspberrypi.org/
> http://director.downloads.raspberrypi.org/raspbian_lite/images/raspbian_lite-2017-12-01/2017-11-29-raspbian-stretch-lite.zip

### 格式化SD卡  
> https://www.sdcard.org/downloads  

### 系统镜像烧录  
> https://sourceforge.net/projects/win32diskimager/  

### 系统镜像制作  

#### Arch  
Arch 安装失败，可能是制作系统盘方法不正确。以后有时间继续……  
tar.gz安装包不能使用win32diskimager 制作系统，需要linux 系统中格式化并分区磁盘，以及安装系统
参考：  
> https://blog.laolilin.com/posts/2016/10/config_raspberry_pi.html#.WiXzdzu-vIV  
> https://pengshp.github.io/post/raspberrypi3-install-archlinux/  
> https://pengshp.github.io/post/Archlinux-arm-config/  

#### Raspbian  

官网下载并解压缩得到img 系统镜像：  
> https://www.raspberrypi.org/  
> http://director.downloads.raspberrypi.org/raspbian_lite/images/raspbian_lite-2017-12-01/2017-11-29-raspbian-stretch-lite.zip  

Window 下使用win32diskimager 制作系统盘（参考其他文档，系统盘推荐大于8GB的MacroSD）

为了节省费用，没有买树莓派配套的饿显示屏。网上搜索文章，树莓派启动系统后默认会自动获取ip（默认账户为：pi 密码：raspberry），也可以配置初始化文件设置固定ip。
正常启动后可通过扫描网络软件（PortScan）或指定ip找到树莓派的机器用远程登录软件（putty）登录即可。
但由于所处网络环境需要登录验证，担心无法正常访问。因此选择使用视频数据线连接显示器。
树莓派自带hdmi 接口，因此购买了hdmi 视频线，但显示器是vga 和dp 接口，因此也买了hdmi 转dp 的转接口。
根据网络文档配置显示器的分辨率等参数，但始终显示器都没有显示。

最终发现，由于树莓派的电压较小，因此转接口必需要购买自带电源的，因此又买了一个hdmi 转vga 的自带电源转接盒。之后系统启动正常。
这里仍然需要注意的是，由于树莓派和转接盒都是很小的设备，因此视频线接口的方向也需要注意，否则也很有可能会发现由于设备上其他接口或接线挡着，导致视频线无法正常插入设备。  
  
配置参考：  
> http://www.cnblogs.com/love-julia/p/3723306.html  
> http://www.shumeipai.net/thread-13936-1-1.html?_dsign=126f6e77  
> http://www.shumeipai.net/thread-13937-1-4.html?_dsign=2517e484  

```
/config.txt  

# uncomment to force a specific HDMI mode (this will force VGA)
#hdmi_group=1
#hdmi_mode=1
```

```
# uncomment if hdmi display is not detected and composite is being output
hdmi_force_hotplug=1

# uncomment to force a specific HDMI mode (this will force VGA)
#hdmi_group=1
hdmi_mode=2
#hdmi_ignore_edid=0xa5000080

# uncomment to force a HDMI mode rather than DVI. This can make audio work in
# DMT (computer monitor) modes
hdmi_drive=2

# uncomment to increase signal to HDMI, if you have interference, blanking, or
# no display
config_hdmi_boost=4
```

### 树莓派信号灯参考(好像已过时，树莓派3B 只有两个灯)  

http://shumeipai.nxez.com/2014/09/30/raspberry-pi-led-status-detail.html  

