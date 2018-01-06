### 单机docker 部署  

### 安装docker  

参考：

> https://github.com/WALL-E/Journey-to-the-docker/issues/6  

> http://python.jobbole.com/87513/  

```
> cat >/etc/yum.repos.d/docker.repo <<-EOF
[dockerrepo]
name=Docker Repository
baseurl=https://yum.dockerproject.org/repo/main/centos/7
enabled=1
gpgcheck=1
gpgkey=https://yum.dockerproject.org/gpg
EOF

> sudo yum install docker-engine
```

参考：https://github.com/WALL-E/Journey-to-the-docker/issues/15

```
setenforce 0

sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://qsofa8am.mirror.aliyuncs.com"],
  "insecure-registries": ["172.28.40.90"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

### 安装tensorflow  

参考：  

> http://tensorfly.cn/  
  
> http://tensorfly.cn/tfdoc/get_started/os_setup.html  
  
> https://docs.caicloud.io/  

> https://github.com/tensorflow/tensorflow  

下载tensorflow docker 镜像并启动  

参考：https://hub.docker.com/r/tensorflow/tensorflow/

> docker pull tensorflow/tensorflow  

> docker run --name tf -it -p 6006:6006 -p 8888:8888 tensorflow/tensorflow  

注意：  

如果启动tensorflow 时有如下警告日志，可尝试执行"setenforce 0"后重启docker 服务后再次尝试启动tensorflow：  

> WARNING: IPv4 forwarding is disabled. Networking will not work.  

可访问官方tensorflow 镜像中自带的jupyter 页面  

> http://172.28.32.203:8888  

执行测试程序

```
> docker exec -it tf bash

root@241c04a0906a:/notebooks# python
Python 2.7.12 (default, Nov 20 2017, 18:23:56) 
[GCC 5.4.0 20160609] on linux2
Type "help", "copyright", "credits" or "license" for more information.
>>> import tensorflow as tf
>>> hello = tf.constant('Hello, TensorFlow!')
>>> sess = tf.Session()
2017-12-25 11:12:02.843899: I tensorflow/core/platform/cpu_feature_guard.cc:137] Your CPU supports instructions that this TensorFlow binary was not compiled to use: SSE4.1 SSE4.2 AVX
>>> sess.run(hello)
'Hello, TensorFlow!'
>>> a = tf.constant(10)
>>> b = tf.constant(32)
>>> sess.run(a + b)
42
>>> sess.close()
>>> exit
Use exit() or Ctrl-D (i.e. EOF) to exit
>>> exit()
```

### 执行神经网络样例程序  

重新启动tensorflow 容器，以便于在宿主机更方便的编辑程序代码。  

> docker run --name tf -it -p 6006:6006 -p 8888:8888 -v /root/data:/data tensorflow/tensorflow  

[例子代码 - Python](./tensorflow/tensorflow-neural-network-sample.py "例子代码 - Python")  

创建代码  

> vi /root/data/tensorflow-neural-network-sample.py  

登入容器  

> docker exec -it tf bash  

执行代码  

> python /data/tensorflow-neural-network-sample.py  
