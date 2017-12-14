### 系统安装  

### 下载系统镜像  
Arch:  
https://archlinuxarm.org/platforms  
选择对应板卡系统版本 - [Raspberry Pi 3]  
https://archlinuxarm.org/platforms/armv8/broadcom/raspberry-pi-3  
http://os.archlinuxarm.org/os/ArchLinuxARM-rpi-3-latest.tar.gz  
  
Raspbian:  
https://www.raspberrypi.org/
http://director.downloads.raspberrypi.org/raspbian_lite/images/raspbian_lite-2017-12-01/2017-11-29-raspbian-stretch-lite.zip

### 格式化SD卡  
https://www.sdcard.org/downloads  

### 系统镜像烧录  
https://sourceforge.net/projects/win32diskimager/  

### 系统镜像制作  

#### Arch  
Arch 安装失败，可能是制作系统盘方法不正确。以后有时间继续……  
tar.gz安装包不能使用win32diskimager 制作系统，需要linux 系统中格式化并分区磁盘，以及安装系统
参考：  
https://blog.laolilin.com/posts/2016/10/config_raspberry_pi.html#.WiXzdzu-vIV  
https://pengshp.github.io/post/raspberrypi3-install-archlinux/  

https://pengshp.github.io/post/Archlinux-arm-config/

#### Raspbian  
/config.txt  

参考：  
http://www.cnblogs.com/love-julia/p/3723306.html  
http://www.shumeipai.net/thread-13936-1-1.html?_dsign=126f6e77  
http://www.shumeipai.net/thread-13937-1-4.html?_dsign=2517e484  

```
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

### 树莓派信号灯参考  

http://shumeipai.nxez.com/2014/09/30/raspberry-pi-led-status-detail.html  

