#!/bin/ksh

cd ..
source lib/common.vars
cd -

function f_fillInBuildConfigVariable {
    cd ~/SilverKing/build 	
    f_fillInVariable_Helper "$BUILD_CONFIG_FILE" "$1" "$2"
    cd ~
}

function f_fillInVariable_Helper {
    typeset filename=$1
    typeset variable=$2
    typeset    value=$3

    sed -ri "s#(${variable}=)#\1$value#" $filename
}

function f_overrideBuildConfigVariable {
    f_overrideVariable_Helper "$BUILD_CONFIG_FILE" "$1" "$2"

}

function f_overrideVariable_Helper {
    typeset filename=$1
    typeset variable=$2
    typeset value=$3

    sed -ri "s#(${variable}=)(\S+\s*.*)(\#)?#\1${value}\3#" $filename
}

### BUILD
cd ~
echo "installing ant"
# sudo yum install ant - is installing 1.8 and we need 1.9
# download binary file @ http://archive.apache.org/dist/ant/binaries/ or www.apache.org/dist/ant/binaries
ant_version=apache-ant-1.10.0
ant_tar=$ant_version-bin.tar.bz2
wget http://archive.apache.org/dist/ant/binaries/$ant_tar
tar -xvf $ant_tar
rm $ant_tar

echo "installing java"
java_version=java-1.8.0-openjdk
sudo yum -y install $java_version-devel.x86_64 # you don't want java-1.8.0-openjdk.x86_64! It really only has the jre's

f_fillInBuildConfigVariable "ANT_9_HOME"  "~/$ant_version"
f_fillInBuildConfigVariable "JAVA_8_HOME" "/usr/lib/jvm/$java_version"

echo "installing zk"
cd ~
zk_version=zookeeper-3.4.11
zk_tar=$zk_version.tar.gz
wget http://apache.claz.org/zookeeper/$zk_version/$zk_tar
tar -xvf $zk_tar
rm $zk_tar

cd $zk_version/conf
echo "tickTime=2000
dataDir=/var/tmp/zookeeper
clientPort=2181" > zoo.cfg

cd ~/.ssh
ssh-keygen -f id_rsa -N '' # flags are to bypass prompt
cat id_rsa.pub >> authorized_keys

f_fillInBuildConfigVariable "SK_REPO_HOME"  '~/$REPO_HOME' # single quotes, so REPO_HOME isn't interpreted

kill_process=kill_process_and_children.pl
cd SilverKing/
cp src/lib/perl/$kill_process bin/
sed -i "s#XXX_PERL_PATH_XXX#/usr/bin/perl#" bin/$kill_process


echo "BUILDING JACE"
sudo yum -y install boost
boost_version=boost_1_61_0
boost_tar=$boost_version.tar.gz
cd ~
wget https://versaweb.dl.sourceforge.net/project/boost/boost/1.61.0/$boost_tar
tar xvf $boost_tar
rm $boost_tar

cd ~
boost_lib=libs/boost
mkdir -p $boost_lib
cd $boost_lib
ln -s /usr/lib64/libboost_thread-mt.so.1.53.0    libboost_thread.so
ln -s /usr/lib64/libboost_date_time-mt.so.1.53.0 libboost_date_time.so
ln -s /usr/lib64/libboost_system-mt.so.1.53.0    libboost_system.so

f_overrideBuildConfigVariable "BOOST_INC" "~/$boost_version"
f_overrideBuildConfigVariable "BOOST_LIB" "~/$boost_lib"

cd ~
curl -O search.maven.org/remotecontent?filepath=com/googlecode/jace/jace-core-runtime/1.2.22/jace-core-runtime-1.2.22.jar
curl -O search.maven.org/remotecontent?filepath=com/googlecode/jace/jace-core-java/1.2.22/jace-core-java-1.2.22.jar 

sudo yum -y install gcc-c++ # for g++
cd ~/SilverKing/build
./build_jace.sh /usr/bin/g++ 

cd ~
jace_lib=libs/jace
mkdir -p $jace_lib
cd $jace_lib
ln -s ../../SilverKing/src/jace/include include
mkdir lib
cd lib
ln -s ../../../jace-core-runtime-1.2.22.jar jace-runtime.jar
ln -s ../../../jace-core-java-1.2.22.jar jace-core.jar
mv ../../../SilverKing/build/silverking-build/silverking-install/arch-output-area/lib/jace/dynamic .
f_fillInBuildConfigVariable "JACE_HOME" "~/$jace_lib"

echo "BUILD CLIENT"
cd ~
f_fillInBuildConfigVariable "GPP"         "/usr/bin/g++"
f_fillInBuildConfigVariable "GCC_LIB"     "/usr/lib/gcc/x86_64-amazon-linux/4.8.5"
f_fillInBuildConfigVariable "JAVA_7_HOME" "/usr/lib/jvm/java-1.7.0-openjdk.x86_64"


echo "BUILD SKFS"
sudo yum install fuse #(/bin/fusermount, /etc/fuse.conf, etc.)
sudo yum install fuse-devel #(.h files, .so)
f_fillInBuildConfigVariable "FUSE_INC"  "/usr/include/fuse"
f_fillInBuildConfigVariable "FUSE_LIB"  "/lib64"

sudo yum install zlib
sudo yum install zlib-devel
f_overrideBuildConfigVariable "ZLIB_INC" "/usr/include"
f_overideBuildConfigVariable  "ZLIB_LIB" "/usr/lib64"

sudo yum install valgrind #(not sure this is necessary)
sudo yum install valgrind-devel #(/usr/include/valgrind/valgrind.h)
f_fillInBuildConfigVariable "VALGRIND_INC" "/usr/include"

# build
sed -i "/Xms/c\return -Xms10M -Xmx\"\+ heapLimits.getV2\(\);" ./src/com/ms/silverking/cloud/dht/management/SKAdmin.java

# build sk
Remove 2 usages of "-exportsymbols" usage
In f_runProxyGenerator function, line with $JAVA_7
In f_generateProxies function, line f_runAutoProxy

# build skfs
edit skfs.config
    silverkingBase, etc..
sudo vi /etc/fuse.conf
    change user_allow

# skc
edit sk.config
    SK_JACE_HOME or skJavaHome 
cd ~/SilverKing/lib
ln -s ../build/silverking-build/jar/silverking.jar

cd ~/SilverKing/build
./build.sh