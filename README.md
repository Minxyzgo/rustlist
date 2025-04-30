# rustlist

一个简单的的第三方铁锈战争列表

## 运行
在`releases`中下载`jar`后，在目录中直接运行`java -jar rustlist-all.jar`

运行后，当前目录会出现一个配置文件 `config.yaml`，以下为配置信息

 - `port`运行的端口
 - `accessToken`调用api时的验证token
 - `staticRoomList`静态的房间列表
 - `updatePeriod`列表更新的时间间隔(单位：毫秒)
 - `roomTick`非静态房间最多可存在的时刻，以`updatePeriod`计算，如填5则代表经过5次`updatePeriod`时间后，该房间将会被删除
 - `enableClientAction`是否允许客户端执行列表操作
 - `selfInfoTimeout` 在`enableClientAction`启用的前提下，查询客户端是否开放的超时时间(单位：毫秒)
 - `clientLimitsPerMinute` 在`enableClientAction`启用的前提下，客户端每分钟访问的最大次数

下面是一个合法的配置文件:
```yaml
port: 8080
accessToken: cSBrUerixWA7aezSfHo9f_LKJkZss2Pj5uJ_4SEoBIw
roomTick: 5
updatePeriod: 2000
enableClientAction: true
staticRoomList:
  - creator: "RELAY-CN"
    mapName: "RELAY-CN"
    version: "1.15"
    playerCurrentCount: 0
    playerMaxCount: 1000
    isUpperCase: true
    netWorkAddress: 43.248.96.172
    port: 5123

  - creator: "GameMaster"
    mapName: "SKY FORTRESS"
    version: "1.15"
    playerCurrentCount: 78
    playerMaxCount: 100
    isUpperCase: true

  - creator: "NoobSlayer"
    mapName: "Underground Lair"
    version: "1.16"
    playerCurrentCount: 42
    playerMaxCount: 64
    isUpperCase: false
```

## 使用
原版铁锈战争并不支持直接切换列表，但可以使用[RWPP](https://github.com/Minxyzgo/RWPP)来读取第三方列表

打开RWPP后，点击多人游戏，点击在右下的`+`
![1](/.github/img/1.png)

之后，在`Server Type`中选择`RoomList`，并填写`url`. (格式示例: http://127.0.0.1:8080/interface?action=list, 注意应为**http**)
![2](/.github/img/2.png)

同样可使用`mt/nt`管理器修改原版文件达到同样的效果，可先提取原版文件安装包，点击`查看`
![t1](/.github/img/t1.png)

点击`classes.dex`后，点击`Dex编辑Plus`
![t2](/.github/img/t2.png)

在搜索中搜索`gs1`，点击搜索结果
![t3](/.github/img/t3.png)

将红色框住部分删除，将蓝色框选部分更改为你的列表地址，例如`http://127.0.0.1:8080` 之后保存安装即可
![t4](/.github/img/t4.png)

## 静态房间列表
`rustlist`在配置文件中添加静态房间列表，即一列**不会**随列表更新而被删除的房间

可以在配置文件中用`staticRoomList`配置

以下是有用的配置信息:

 - `creator`房间的创建者
 - `mapName`房间地图
 - `version`版本号
 - `playerCurrentCount`当前房间的玩家数量
 - `playerMaxCount`最大玩家数量
 - `isUpperCase`是否突出显示
 - `netWorkAddress`网络地址
 - `port`端口
 - `mods`模组列表

## Api
可以利用`api`来动态添加房间

注意，需要先运行jar在配置文件中获得`accessToken`后在请求中添加

这是一个请求的示例:
```js
const response = await fetch(`http://127.0.0.1:${config.port}/api/update?isStatic=true`, {
    method: 'POST',
    headers: {
        'Authorization': `Bearer ${config.accessToken}`,
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        uuid: "ac6d00d9-f264-4446-89c9-38e5dcd93cc8"
    })
});
```

### 更新列表: post /api/update

**参数：**
 - `isStatic` 是否为静态房间

**请求：**
```json
{
    "uuid": "ac6d00d9-f264-4446-89c9-38e5dcd93cc8",
    "roomOwner": "",
    "gameVersion": 176,
    "netWorkAddress": "",
    "localAddress": "",
    "port": 5123,
    "isOpen": true,
    "creator": "",
    "requiredPassword": false,
    "mapName": "",
    "mapType": "",
    "status": "battleroom",
    "version": "",
    "isLocal": false,
    "displayMapName": "",
    "playerCurrentCount": null,
    "playerMaxCount": null,
    "isUpperCase": false,
    "uuid2": "",
    "unknown": false,
    "mods": ""
}
```
### 维持房间: get /api/keep
重置一个房间的tick

**参数：**
- `uuid` 房间uuid

### 删除房间: get /api/remove
删除一个在列表上的房间

**参数：**
- `uuid` 房间uuid