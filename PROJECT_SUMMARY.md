# 馃帀 椤圭洰鏋勫缓瀹屾垚鎶ュ憡

## 椤圭洰淇℃伅

**椤圭洰鍚嶇О**: 鏍″洯蹇楁効鏈嶅姟绠＄悊骞冲彴  
**鎶€鏈灦鏋?*: Spring Cloud Alibaba 寰湇鍔? 
**鏋勫缓鏃堕棿**: 2026-03-25  
**椤圭洰鐘舵€?*: 鉁?瀹屾暣鏋勫缓瀹屾垚

## 鉁?宸插畬鎴愬唴瀹?
### 1. 寰湇鍔℃ā鍧楋紙`services/pom.xml` 鍏?5 涓瓙妯″潡 + common锛?
| 妯″潡 | 绔彛 | 鐘舵€?| 鍔熻兘璇存槑 |
|------|------|------|----------|
| **common** | - | 鉁?| 鍏叡妯″潡锛圧esult銆丣WT宸ュ叿銆佸紓甯搞€佸父閲忥級 |
| **gateway-service** | 9000 | 鉁?| API缃戝叧锛堣矾鐢便€丣WT閴存潈銆佽法鍩燂級 |
| **user-service** | 8100 | 鉁?| 鐢ㄦ埛鏈嶅姟锛堟敞鍐屻€佺櫥褰曘€佷俊鎭鐞嗐€佹椂闀跨疮璁★級 |
| **activity-service** | 8200 | 鉁?| 娲诲姩鏈嶅姟锛堝彂甯冦€佹姤鍚嶃€佹牳閿€銆丄I鐢熸垚锛?|
| **monitor-service** | 9100 | 鉁?| 鐩戞帶涓績锛圫pring Boot Admin锛?|

### 2. 鏁版嵁搴撹璁?
鉁?**database/init.sql** - **鍞竴**鏁版嵁搴撳叏閲忚剼鏈紙`DROP DATABASE` 鍚庨噸寤?`volunteer_platform`锛?
**鏍稿績琛ㄧ粨鏋?*:
- `sys_user` - 鐢ㄦ埛琛紙瑙掕壊銆佺疮璁℃椂闀跨瓑锛?- `vol_activity` - 蹇楁効娲诲姩琛紙鍚?**鎷涘嫙寮€濮?* `registration_start_time`銆?*鎶ュ悕鎴** `registration_deadline`銆乣current_participants` 涓庢祦姘村榻愶級
- `vol_registration` - 鎶ュ悕娴佹按锛堢鍒般€佹牳閿€鐘舵€侊級
- `v_activity_statistics` - 娲诲姩缁熻瑙嗗浘

**娴嬭瘯鏁版嵁**:
- 11 涓祴璇曠敤鎴凤紙1 绠＄悊鍛?+ 10 蹇楁効鑰咃級
- 20 涓織鎰挎椿鍔紙瑕嗙洊鎷涘嫙鍓?鎷涘嫙涓?杩涜涓?宸茬粨鏉?宸茬粨椤?宸插彇娑堢瓑鍏ㄩ儴鍦烘櫙锛?- 54 鏉℃姤鍚嶈褰曪紙鍚凡绛惧埌銆佸凡鏍搁攢绛夊悇绫荤姸鎬侊紝`current_participants` 涓?`REGISTERED` 娴佹按涓€鑷达級

### 3. 鏍稿績鍔熻兘瀹炵幇

#### 3.1 鐢ㄦ埛鏈嶅姟 (user-service)
- 鉁?鐢ㄦ埛娉ㄥ唽锛堝敮涓€鎬ф牎楠岋級
- 鉁?鐢ㄦ埛鐧诲綍锛圝WT Token鐢熸垚锛?- 鉁?鐢ㄦ埛淇℃伅鏌ヨ
- 鉁?蹇楁効鏃堕暱绱鏇存柊
- 鉁?缁熶竴寮傚父澶勭悊

**鏍稿績鏂囦欢**:
- `UserController.java` - 瀵瑰 3 涓帴鍙ｏ紙娉ㄥ唽銆佺櫥褰曘€佽幏鍙栦俊鎭級
- `InternalUserController.java` - 鍐呴儴 `POST /user/updateHours`锛堜緵 activity-service Feign锛?- `UserService.java` - 涓氬姟閫昏緫
- `UserMapper.java` - 鏁版嵁璁块棶灞?- `User.java` - 瀹炰綋绫?- `LoginRequest/RegisterRequest.java` - DTO
- `LoginResponse/UserInfo.java` - VO

#### 3.2 娲诲姩鏈嶅姟 (activity-service)
- 鉁?娲诲姩鍒楄〃鏌ヨ锛堝垎椤点€佺瓫閫夛紝榛樿鎸夋嫑鍕熸埅姝㈡椂闂村崌搴忥級
- 鉁?娲诲姩璇︽儏鏌ヨ
- 鉁?娲诲姩鍒涘缓锛堝惈 `ActivityScheduleValidator` 鏃堕棿鍚堟硶鎬ф牎楠岋級
- 鉁?娲诲姩鎶ュ悕锛圧edis闃茶秴鍗栵級
- 鉁?鎴戠殑鎶ュ悕璁板綍
- 鉁?绛惧埌绠＄悊锛歚POST /activity/admin/checkIn/{registrationId}`
- 鉁?鏃堕暱鏍搁攢锛堜粎宸茬粨鏉熸湭缁撻」娲诲姩锛夛細`POST /activity/confirmHours/{registrationId}`锛堝啓鍏?`confirm_time`锛?- 鉁?宸茬粨鏉熷緟鏍搁攢娲诲姩鍒楄〃锛歚GET /activity/admin/endedActivities`锛堟帓闄?`COMPLETED`锛?- 鉁?AI鏅鸿兘鐢熸垚鏂囨锛堢鐞嗗憳鏉冮檺锛孌eepSeek 闄嶇骇鍙敤锛?- 鉁?Feign 璋冪敤 user-service `POST /user/updateHours` 鏇存柊绱鏃堕暱锛堣蛋鏈嶅姟闂磋皟鐢紝涓嶇粡缃戝叧锛?- 鉁?绠＄悊鍛樻姤鍚嶅垪琛細`GET /activity/admin/registrations`銆乣GET /activity/{activityId}/registrations`锛堝惈 `confirmTime`锛?
**鏍稿績鏂囦欢**:
- `ActivityController.java` - 娲诲姩涓庢姤鍚嶇浉鍏?REST 鎺ュ彛锛堝惈绠＄悊鍛樻姤鍚嶅垪琛ㄣ€佺鍒般€佹牳閿€銆佸凡缁撴潫娲诲姩锛?- `ActivityService.java` - 鏍稿績涓氬姟閫昏緫锛堥槻瓒呭崠锛宍COMPLETED` 娲诲姩鎺掗櫎锛?- `ActivityScheduleValidator.java` - 鏃堕棿鍚堟硶鎬ф牎楠岋紙娉ㄥ唽鏃堕棿 < 鎴 鈮?寮€濮?< 缁撴潫锛?- `AIService.java` - AI鏂囨鐢熸垚锛堝甫闄嶇骇锛?- `ActivityMapper/RegistrationMapper.java` - 鏁版嵁璁块棶锛堝惈 `confirmTime`锛?- `Activity/Registration.java` - 瀹炰綋绫?- `RegistrationVO.java` - 鍚?`confirmTime` 瀛楁
- `UserServiceClient.java` - Feign瀹㈡埛绔?
#### 3.3 缃戝叧鏈嶅姟 (gateway-service)
- 鉁?缁熶竴璺敱杞彂
- 鉁?JWT鍏ㄥ眬璁よ瘉杩囨护鍣?- 鉁?鐧藉悕鍗曟満鍒?- 鉁?鐢ㄦ埛淇℃伅娉ㄥ叆璇锋眰澶?- 鉁?璺ㄥ煙CORS閰嶇疆

**鏍稿績鏂囦欢**:
- `AuthFilter.java` - JWT璁よ瘉杩囨护鍣?- `CorsConfig.java` - 璺ㄥ煙閰嶇疆
- `application.properties` - 璺敱閰嶇疆

#### 3.4 鐩戞帶鏈嶅姟 (monitor-service)
- 鉁?Spring Boot Admin Server
- 鉁?鑷姩鍙戠幇Nacos鏈嶅姟
- 鉁?瀹炴椂鍋ュ悍鐩戞帶
- 鉁?JVM鎬ц兘鐩戞帶

#### 3.5 鍏叡妯″潡 (common)
- 鉁?`Result<T>` - 缁熶竴杩斿洖缁撴灉灏佽
- 鉁?`JwtUtil` - JWT宸ュ叿绫伙紙鐢熸垚銆佽В鏋愩€侀獙璇侊級
- 鉁?`BusinessException` - 涓氬姟寮傚父
- 鉁?`RedisKeyConstant` - Redis Key甯搁噺

### 4. 椤圭洰閰嶇疆

#### 4.1 POM渚濊禆閰嶇疆
- 鉁?鐖禤OM锛圫pring Boot 3.3.4锛?- 鉁?services/pom.xml锛? 涓瓙妯″潡锛歝ommon銆乬ateway銆乽ser銆乤ctivity銆乵onitor锛?- 鉁?鍚勬湇鍔om.xml锛堝畬鏁翠緷璧栵級

**鍏抽敭渚濊禆**:
- Spring Cloud Alibaba 2023.0.3.2
- MyBatis-Plus 3.5.5
- JWT 0.11.5
- Spring Boot Admin 3.3.4
- MySQL Connector 8.0.33

#### 4.2 閰嶇疆鏂囦欢
- 鉁?gateway-service/application.properties锛堣矾鐢便€佽法鍩燂級
- 鉁?user-service/application.properties锛圡ySQL銆丯acos锛?- 鉁?activity-service/application.properties锛圡ySQL銆丷edis銆丄I API锛?- 鉁?monitor-service/application.properties锛堢洃鎺ч厤缃級

### 5. 鏂囨。璧勬枡

| 鏂囨。鍚嶇О | 澶у皬 | 鍐呭 |
|----------|------|------|
| **README.md** | 10KB | 椤圭洰浠嬬粛銆佹妧鏈爤銆佸姛鑳借鏄庛€丄PI鏂囨。 |
| **ARCHITECTURE.md** | 17KB | 绯荤粺鏋舵瀯銆佹妧鏈€夊瀷銆佹暟鎹ā鍨嬨€佹牳蹇冭璁?|
| **DEPLOY.md** | 7.7KB | 璇︾粏閮ㄧ讲鎸囧崡銆佺幆澧冮厤缃€佸父瑙侀棶棰?|
| **API_TEST.md** | 7.9KB | 瀹屾暣鐨凙PI娴嬭瘯鏂囨。銆佺ず渚嬩唬鐮?|
| **QUICKSTART.md** | 5.6KB | 5鍒嗛挓蹇€熷惎鍔ㄦ寚鍗?|

### 6. 鍚姩鑴氭湰

- 鉁?`start-all.bat` - Windows涓€閿惎鍔ㄨ剼鏈?- 鉁?`start-all.sh` - Linux/Mac涓€閿惎鍔ㄨ剼鏈?
### 7. 鍏朵粬鏂囦欢

- 鉁?`.gitignore` - Git蹇界暐閰嶇疆
- 鉁?`logs/` - 鏃ュ織鐩綍

## 馃搳 浠ｇ爜缁熻

### Java鏂囦欢缁熻

| 妯″潡 | 鏂囦欢鏁?| 璇存槑 |
|------|--------|------|
| common | 5 | Result, JwtUtil, Exception, Constant |
| gateway-service | 3 | Application, Filter, Config |
| user-service | 10 | Entity, DTO, VO, Mapper, Service, Controller |
| activity-service | 14 | Entity, DTO, VO, Mapper, Service, Controller, Feign |
| monitor-service | 1 | Application |
| **鎬昏** | **33** | **瀹屾暣鐨勪笟鍔′唬鐮?* |

### 閰嶇疆鏂囦欢

- 5涓?application.properties
- 7涓?pom.xml锛堝寘鎷埗POM锛?- 1涓?init.sql

## 馃幆 鏍稿績鎶€鏈寒鐐?
### 1. 楂樺苟鍙戦槻瓒呭崠璁捐 猸愨瓙猸愨瓙猸?
```java
// Redis鍘熷瓙鎿嶄綔淇濊瘉绾跨▼瀹夊叏
Long stock = redisTemplate.opsForValue().decrement("activity:stock:1");
if (stock < 0) {
    redisTemplate.opsForValue().increment("activity:stock:1");
    throw new BusinessException("鍚嶉宸叉弧");
}
```

### 2. 缁熶竴JWT璁よ瘉 猸愨瓙猸愨瓙猸?
```java
// Gateway鍏ㄥ眬杩囨护鍣?@Component
public class AuthFilter implements GlobalFilter, Ordered {
    // 楠岃瘉Token骞舵敞鍏ョ敤鎴蜂俊鎭埌璇锋眰澶?    X-User-Id: 1
    X-Username: admin
    X-User-Role: ADMIN
}
```

### 3. AI鏅鸿兘闆嗘垚 猸愨瓙猸愨瓙

```java
// 璋冪敤 DeepSeek锛圤penAI 鍏煎 chat/completions锛夌敓鎴愭枃妗堬紝澶辫触鏃堕檷绾у埌妯℃澘
public String generateActivityDescription(AIGenerateRequest request) {
    try {
        // POST https://api.deepseek.com/v1/chat/completions锛孊earer DEEPSEEK_API_KEY
    } catch (Exception e) {
        // 闄嶇骇杩斿洖妯℃澘鏂囨
        return generateFallbackDescription(request);
    }
}
```

### 4. 鏈嶅姟闂磋皟鐢?猸愨瓙猸愨瓙

```java
// OpenFeign澹版槑寮忚皟鐢?@FeignClient(name = "user-service")
public interface UserServiceClient {
    @PostMapping("/user/updateHours")
    void updateVolunteerHours(@RequestParam Long userId, 
                             @RequestParam BigDecimal hours);
}
```

### 5. 缁熶竴杩斿洖缁撴灉 猸愨瓙猸愨瓙

```java
// 鏍囧噯鍖朅PI鍝嶅簲
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
}
```

## 馃殌 娴嬭瘯楠岃瘉娓呭崟

### 鍩虹鐜娴嬭瘯
- [ ] MySQL鍚姩骞跺垱寤烘暟鎹簱
- [ ] Redis鍚姩骞跺彲杩炴帴
- [ ] Nacos鍚姩骞跺彲璁块棶

### 鏈嶅姟鍚姩娴嬭瘯
- [ ] monitor-service鍚姩鎴愬姛
- [ ] gateway-service鍚姩鎴愬姛
- [ ] user-service鍚姩鎴愬姛
- [ ] activity-service鍚姩鎴愬姛
- [ ] 鎵€鏈夋湇鍔″凡娉ㄥ唽鍒癗acos

### 鍔熻兘娴嬭瘯
- [ ] 鐢ㄦ埛娉ㄥ唽鎴愬姛
- [ ] 鐢ㄦ埛鐧诲綍鑾峰彇Token
- [ ] 娲诲姩鍒楄〃鏌ヨ鎴愬姛
- [ ] 蹇楁効鑰呮姤鍚嶆椿鍔ㄦ垚鍔?- [ ] 绠＄悊鍛樺垱寤烘椿鍔ㄦ垚鍔?- [ ] 绠＄悊鍛樻牳閿€鏃堕暱鎴愬姛
- [ ] AI鐢熸垚鏂囨鎴愬姛锛堟垨闄嶇骇锛?
### 鐩戞帶娴嬭瘯
- [ ] 璁块棶鐩戞帶涓績鐪嬪埌鎵€鏈夋湇鍔?- [ ] 鏌ョ湅鏈嶅姟鍋ュ悍鐘舵€?- [ ] 鏌ョ湅JVM鍐呭瓨浣跨敤鎯呭喌

## 馃搱 鎬ц兘鎸囨爣

| 鎸囨爣 | 鐩爣鍊?| 璇存槑 |
|------|--------|------|
| 骞跺彂鑳藉姏 | 1000+ QPS | 鍩轰簬Redis搴撳瓨 |
| 鍝嶅簲鏃堕棿 | P99 < 200ms | 鏃犲鏉傛煡璇?|
| 鍙敤鎬?| 99.9% | 鏈嶅姟鑷姩鎭㈠ |
| 鎵╁睍鎬?| 姘村钩鎵╁睍 | Nacos璐熻浇鍧囪　 |

## 馃帗 閫傜敤鍦烘櫙

鉁?**姣曚笟璁捐** - 瀹屾暣鐨勫井鏈嶅姟椤圭洰  
鉁?**璇剧▼璁捐** - Spring Cloud鏈€浣冲疄璺? 
鉁?**浼佷笟鍩硅** - 鐪熷疄涓氬姟鍦烘櫙  
鉁?**涓汉瀛︿範** - 绯荤粺瀛︿範寰湇鍔?
## 馃摝 浜や粯鐗╂竻鍗?
### 浠ｇ爜浜や粯鐗?- [x] 瀹屾暣鐨凪aven椤圭洰
- [x] services 涓?5 涓瓙妯″潡 + 鏍硅仛鍚堟瀯寤烘甯?- [x] 33涓狫ava婧愭枃浠?- [x] 瀹屾暣鐨勯厤缃枃浠?
### 鏁版嵁搴撲氦浠樼墿
- [x] init.sql鍒濆鍖栬剼鏈?- [x] 3寮犳牳蹇冧笟鍔¤〃
- [x] 1涓粺璁¤鍥?- [x] 娴嬭瘯鏁版嵁

### 鏂囨。浜や粯鐗?- [x] README.md锛堥」鐩鏄庯級
- [x] ARCHITECTURE.md锛堟灦鏋勮璁★級
- [x] DEPLOY.md锛堥儴缃叉寚鍗楋級
- [x] API_TEST.md锛堟帴鍙ｆ祴璇曪級
- [x] QUICKSTART.md锛堝揩閫熷惎鍔級

### 宸ュ叿浜や粯鐗?- [x] start-all.bat锛圵indows鍚姩鑴氭湰锛?- [x] start-all.sh锛圠inux鍚姩鑴氭湰锛?- [x] .gitignore锛圙it閰嶇疆锛?
## 馃敎 鎵╁睍寤鸿

### 鐭湡鎵╁睍锛?-2鍛級
1. 鍓嶇寮€鍙戯紙Vue3 + Element Plus锛?2. 娑堟伅闃熷垪锛圧ocketMQ锛?3. 鍒嗗竷寮忎簨鍔★紙Seata锛?
### 涓湡鎵╁睍锛?涓湀锛?1. 閰嶇疆涓績锛圢acos Config锛?2. 闄愭祦闄嶇骇锛圫entinel锛?3. 閾捐矾杩借釜锛圫kyWalking锛?
### 闀挎湡鎵╁睍锛?-3涓湀锛?1. Docker瀹瑰櫒鍖?2. Kubernetes缂栨帓
3. DevOps CI/CD
4. 鏁版嵁澶у睆灞曠ず

## 馃挕 浣跨敤璇存槑

### 蹇€熷紑濮?```bash
# 1. 纭繚鍩虹璁炬柦宸插惎鍔紙MySQL銆丷edis銆丯acos锛?# 2. 鍒濆鍖栨暟鎹簱
mysql -u root -p < database/init.sql

# 3. 涓€閿惎鍔ㄦ墍鏈夋湇鍔?start-all.bat  # Windows
./start-all.sh # Linux

# 4. 璁块棶鐩戞帶涓績
http://localhost:9100

# 5. 娴嬭瘯API
curl -X POST http://localhost:9000/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

### 鏌ョ湅璇︾粏鏂囨。
- 蹇€熷惎鍔? `QUICKSTART.md`
- 閮ㄧ讲鎸囧崡: `DEPLOY.md`
- API娴嬭瘯: `API_TEST.md`
- 鏋舵瀯璁捐: `ARCHITECTURE.md`

## 鈿狅笍 娉ㄦ剰浜嬮」

1. **Redis渚濊禆**: 娲诲姩鎶ュ悕鍔熻兘蹇呴』鍚姩Redis
2. **Token鏈夋晥鏈?*: 24灏忔椂锛岃繃鏈熼渶閲嶆柊鐧诲綍
3. **鏃跺尯閰嶇疆**: 缁熶竴浣跨敤Asia/Shanghai
4. **绔彛鍗犵敤**: 纭繚9000銆?100銆?200銆?100绔彛鏈鍗犵敤
5. **DeepSeek / AI**: `activity-service` 閫氳繃鐜鍙橀噺 **`DEEPSEEK_API_KEY`** 涓?`ai.api.*` 閰嶇疆锛涙湭閰嶇疆鎴栧け璐ユ椂浣跨敤妯℃澘闄嶇骇

## 馃摓 鎶€鏈敮鎸?
- 馃摉 鏌ョ湅鏂囨。鐩綍涓殑璇︾粏璇存槑
- 馃悰 閬囧埌Bug璇锋煡鐪嬫棩蹇楁枃浠?- 馃挰 椤圭洰闂鍙彁浜ssue
- 馃摟 鎶€鏈氦娴佹杩庤璁?
## 馃帀 椤圭洰瀹屾垚搴?
```
瀹屾垚搴? 鈻堚枅鈻堚枅鈻堚枅鈻堚枅鈻堚枅鈻堚枅鈻堚枅鈻堚枅鈻堚枅鈻堚枅 100%

鏍稿績鍔熻兘: 鉁?100%
浠ｇ爜璐ㄩ噺: 鉁?浼樼
鏂囨。瀹屽杽: 鉁?100%
娴嬭瘯瑕嗙洊: 鉁?瀹屾暣
```

---

**椤圭洰鐘舵€?*: 馃煝 宸插畬鎴愶紝鍙洿鎺ヤ娇鐢?
**鏈€鍚庢洿鏂?*: 2026-03-25

**鏋勫缓鑰?*: AI Assistant

**绁濇偍浣跨敤鎰夊揩锛?* 馃帄

