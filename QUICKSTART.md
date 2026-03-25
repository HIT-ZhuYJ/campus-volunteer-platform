# 馃殌 蹇€熷紑濮嬫寚鍗?
鏈寚鍗楀皢甯姪浣犲湪 **10 鍒嗛挓鍐?* 蹇€熷惎鍔ㄦ牎鍥織鎰挎湇鍔＄鐞嗗钩鍙般€?
## 馃搵 鍓嶇疆妫€鏌ユ竻鍗?
鍦ㄥ紑濮嬩箣鍓嶏紝璇风‘淇濆凡瀹夎浠ヤ笅杞欢锛?
- 鉁?JDK 17+
- 鉁?Maven 3.6+
- 鉁?MySQL 8.0+
- 鉁?Redis 5.0+
- 鉁?Nacos 2.x
- 鉁?Node.js 16+锛堝墠绔級

---

## 馃幆 蹇€熷惎鍔ㄦ楠?
### 姝ラ 1锛氬垵濮嬪寲鏁版嵁搴擄紙2 鍒嗛挓锛?
1. 鍚姩 MySQL 鏈嶅姟

2. 瀵煎叆鏁版嵁搴?```bash
# 鏂瑰紡1锛氬懡浠よ瀵煎叆
mysql -u root -p < database/init.sql

# 鏂瑰紡2锛歁ySQL Workbench / Navicat
# 鎵撳紑 database/init.sql 鏂囦欢锛屾墽琛屽叏閮ㄨ鍙?```

> **閲嶈**锛歚init.sql` 浼氬厛 **`DROP DATABASE IF EXISTS volunteer_platform`**锛屽啀閲嶅缓搴撱€佽〃銆佺储寮曘€佽鍥句笌娴嬭瘯鏁版嵁銆傝嫢璇ュ簱鍚嶅湪鏈満宸叉湁閲嶈鏁版嵁锛岃鍏堝浠姐€?
3. 楠岃瘉鏁版嵁搴?```sql
USE volunteer_platform;
SHOW TABLES;
-- 鏈熸湜鐪嬪埌锛歴ys_user銆乿ol_activity銆乿ol_registration銆乿_activity_statistics锛堣鍥撅級
SELECT COUNT(*) FROM sys_user;        -- 搴斾负 11锛? 绠＄悊鍛?+ 10 蹇楁効鑰咃級
SELECT COUNT(*) FROM vol_activity;    -- 搴斾负 20
SELECT COUNT(*) FROM vol_registration; -- 搴斾负 54
```

4. 淇敼鏁版嵁搴撹繛鎺ュ瘑鐮侊紙榛樿涓?`123888`锛?
鑻ラ渶瑕佷慨鏀癸紝缂栬緫鍚勬湇鍔＄殑 `src/main/resources/application.properties`锛?```properties
spring.datasource.password=浣犵殑瀵嗙爜
```

---

### 姝ラ 2锛氬惎鍔?Redis锛? 鍒嗛挓锛?
```bash
# Windows
redis-server.exe redis.windows.conf

# Linux/Mac
redis-server

# 楠岃瘉锛堝簲杩斿洖 PONG锛?redis-cli ping
```

---

### 姝ラ 3锛氬惎鍔?Nacos锛? 鍒嗛挓锛?
```bash
# Windows
cd nacos/bin
startup.cmd -m standalone

# Linux/Mac
cd nacos/bin
sh startup.sh -m standalone
```

楠岃瘉锛氳闂?http://localhost:8848/nacos锛堣处鍙?瀵嗙爜锛歚nacos/nacos`锛?
---

### 姝ラ 4锛氱紪璇戝悗绔」鐩紙2 鍒嗛挓锛?
鍦?`services` 鐩綍涓嬫墽琛岋細
```bash
cd services
mvn clean install -DskipTests
```

鎴栧湪浠撳簱鏍圭洰褰曪細
```bash
mvn clean install -DskipTests
```

---

### 姝ラ 5锛氬惎鍔ㄥ悗绔湇鍔★紙鎸夐『搴忥紝3 鍒嗛挓锛?
#### 5.1 user-service锛堢鍙?8100锛?```bash
cd services/user-service
mvn spring-boot:run
```
楠岃瘉锛氭棩蹇楀嚭鐜?`nacos registry ... user-service ... register finished`

#### 5.2 activity-service锛堢鍙?8200锛?```bash
cd services/activity-service
mvn spring-boot:run
```
> **AI 鏂囨鍔熻兘**锛氳嫢闇€浣跨敤 AI 鐢熸垚娲诲姩鎻忚堪锛屽湪鍚姩鍓嶈缃幆澧冨彉閲?`DEEPSEEK_API_KEY`锛涙湭璁剧疆鍒欒嚜鍔ㄥ洖閫€鍒板唴缃ā鏉裤€?
#### 5.3 gateway-service锛堢鍙?9000锛?```bash
cd services/gateway-service
mvn spring-boot:run
```
楠岃瘉锛氭棩蹇楀嚭鐜?`Netty started on port 9000`

#### 5.4 monitor-service锛堢鍙?9100锛屽彲閫夛級
```bash
cd services/monitor-service
mvn spring-boot:run
```

---

### 姝ラ 6锛氬惎鍔ㄥ墠绔紙2 鍒嗛挓锛?
```bash
cd frontend
npm install
npm run dev
```

楠岃瘉锛氱粓绔緭鍑?`Local: http://localhost:3000/`锛屾祻瑙堝櫒鎵撳紑鍗冲彲銆?
---

## 鉁?楠岃瘉瀹夎

### 1. 妫€鏌?Nacos 鏈嶅姟娉ㄥ唽

璁块棶 http://localhost:8848/nacos 鈫?"鏈嶅姟绠＄悊" 鈫?"鏈嶅姟鍒楄〃"锛屽簲鐪嬪埌锛?- 鉁?`user-service`
- 鉁?`activity-service`
- 鉁?`gateway-service`
- 鉁?`monitor-service`锛堝鏋滃惎鍔ㄤ簡锛?
### 2. 娴嬭瘯鐧诲綍鎺ュ彛

```bash
curl -X POST http://localhost:9000/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
# 杩斿洖 token 瀛楁鍗充负鎴愬姛
```

### 3. 璁块棶鍓嶇

鎵撳紑娴忚鍣細http://localhost:3000

浣犲簲璇ョ湅鍒版笎鍙樿儗鏅?+ 鍔ㄦ€佹诞鍔ㄥ厓绱犵殑鐜颁唬鍖栫櫥褰曢〉闈紝搴曢儴鏈変笁涓揩閫熺櫥褰曟寜閽€?
---

## 馃帀 娴嬭瘯璐﹀彿

鎵€鏈夎处鍙峰瘑鐮佸潎涓?`password123`銆?
### 绠＄悊鍛樿处鍙?
| 璐﹀彿 | 濮撳悕 | 瑙掕壊 |
|------|------|------|
| admin | 绠＄悊鍛?| ADMIN |

### 蹇楁効鑰呰处鍙凤紙鍏?10 涓級

| 璐﹀彿 | 濮撳悕 | 瀛﹀彿 | 绱鏃堕暱 | 璇存槑 |
|------|------|------|---------|------|
| student01 | 寮犱笁 | 2021101 | 11.00h | 鍙備笌娲诲姩鏈€澶?|
| student02 | 鏉庡洓 | 2021102 | 6.00h  | |
| student03 | 鐜嬩簲 | 2021103 | 6.50h  | |
| student04 | 璧靛叚 | 2021104 | 7.00h  | |
| student05 | 闄堜竷 | 2021105 | 6.50h  | |
| student06 | 瀛欏叓 | 2021106 | 5.50h  | |
| student07 | 鍛ㄤ節 | 2021107 | 0.00h  | 鏂扮敤鎴凤紝鏈夋姤鍚嶄絾鏃犳牳閿€ |
| student08 | 鍚村崄 | 2021108 | 0.00h  | 鏂扮敤鎴?|
| student09 | 閮戝崄涓€ | 2021109 | 0.00h | 鏂扮敤鎴?|
| student10 | 鐜嬪崄浜?| 2021110 | 0.00h | 鏂扮敤鎴?|

---

## 馃梻锔?娴嬭瘯鏁版嵁鍦烘櫙璇存槑

`database/init.sql` 涓唴缃簡 **20 涓椿鍔?*锛岃鐩栨墍鏈変笟鍔＄姸鎬侊紝鏂逛究閫愪竴娴嬭瘯锛?
| 鍦烘櫙 | 娲诲姩ID | 璇存槑 |
|------|--------|------|
| **鎷涘嫙涓?*锛堥椤?娲诲姩鍒楄〃鍙锛?| 1锝? | 鎴鏃ワ細3/28銆?/30銆?/5銆?/10銆?/20锛屾寜绱ц揩搴︽帓搴?|
| **鎷涘嫙鏈紑濮?* | 6銆? | 鎷涘嫙绐楀彛鍒嗗埆鍦?5 鏈堛€? 鏈堝紑鏀?|
| **鎷涘嫙鎴锛屾椿鍔ㄦ湭寮€濮?* | 8銆? | 鎶ュ悕绐楀彛宸插叧闂絾娲诲姩灏氭湭涓捐 |
| **娲诲姩杩涜涓?*锛堢鍒版ā鍧楀彲瑙侊級 | 10銆?1 | 娲诲姩 10 = 浠婃棩鍏ㄥぉ锛涙椿鍔?11 璺ㄤ笁澶╋紝鍚勬湁绛惧埌/鏈鍒拌褰?|
| **宸茬粨鏉燂紝寰呮牳閿€**锛堟椂闀挎牳閿€妯″潡鍙锛?| 12銆?3銆?4 | 鏈夌殑宸查儴鍒嗘牳閿€锛屾湁鐨勫皻鏈牳閿€锛屾祴璇曟牳閿€鎿嶄綔 |
| **宸茬粨椤?COMPLETED** | 15锝?8 | 15鈥?7 鍏ㄥ憳瀹岀粨锛?8 閮ㄥ垎寰呮牳閿€锛堟紨绀虹粨椤瑰悗涓嶅彲鍐嶉€夛級 |
| **宸插彇娑?CANCELLED** | 19銆?0 | 涓嶅嚭鐜板湪浠讳綍鎿嶄綔妯″潡涓?|

---

## 馃枼锔?绯荤粺鍔熻兘璇存槑

### 蹇楁効鑰呯鍔熻兘

#### 棣栭〉锛坄/home`锛?- 灞曠ず涓汉缁熻锛氱疮璁℃椂闀裤€佸弬涓庢椿鍔ㄦ暟銆?*宸茬鍒?*鏁般€佸凡鏍搁攢鏁?- "鏈€鏂版椿鍔?鍖哄煙浠呮樉绀?*褰撳墠澶勪簬鎷涘嫙绐楀彛鍐?*鐨勬椿鍔紝鎸夋姤鍚嶆埅姝㈡棩鍗囧簭鎺掑垪锛堟渶绱ф€ヤ紭鍏堬級

#### 娲诲姩鍒楄〃锛坄/activities`锛?- 鏀寔鎸?*娲诲姩鐘舵€?*銆?*鎷涘嫙闃舵**銆?*娲诲姩绫诲瀷**涓夌粍鏉′欢绛涢€夛紝浜や簰閲囩敤鑳跺泭 Pill 鏍峰紡
- 鍏ㄩ儴娲诲姩鎸?*鎶ュ悕鎴鏃堕棿鍗囧簭**鎺掑垪锛屾渶蹇埅姝㈢殑鎺掑湪鏈€鍓?- 琛ㄦ牸鐩存帴鏄剧ず鎶ュ悕杩涘害鏉★紝鍚嶉鍛婃€ユ椂楂樹寒绾㈣壊

#### 娲诲姩璇︽儏锛坄/activity/:id`锛?- 椤堕儴 Hero 鍖猴細娲诲姩鏍囬銆侀樁娈垫爣绛俱€佺被鍒爣绛?- 鍙充晶锛氬悕棰濊繘搴︺€佹姤鍚嶆搷浣溿€佸織鎰挎椂闀?- 鎶ュ悕鎸夐挳浠呭湪"鎷涘嫙涓?鐘舵€佷笖鏈夊墿浣欏悕棰濇椂鍙敤

#### 涓汉涓績锛坄/my`锛?- 涓汉淇℃伅鍗＄墖锛堝甫娓愬彉澶村儚鍖猴級
- 4 椤圭粺璁★細绱鏃堕暱 / 鍙備笌娲诲姩 / 宸茬鍒?/ 宸叉牳閿€
- 蹇楁効瓒宠抗琛ㄦ牸锛氬睍绀哄叏閮ㄦ姤鍚嶈褰曞強绛惧埌銆佹牳閿€鐘舵€?
---

### 绠＄悊鍛樼鍔熻兘锛坄/admin/*`锛?
#### 娲诲姩绠＄悊锛坄/admin/activities`锛?- 灞曠ず鍏ㄩ儴娲诲姩锛?*宸茬粨椤癸紙缁胯壊鑳屾櫙锛?* 鍜?**宸插彇娑堬紙绾㈣壊鍒犻櫎绾匡級** 鏈夋槑鏄捐瑙夊尯鍒?- 鎿嶄綔鎸夐挳锛氭煡鐪?/ 缂栬緫 / 鍙栨秷娲诲姩 / **缁撻」** / 鍒犻櫎
- 缂栬緫銆佸彇娑堛€佺粨椤规寜閽粎瀵?`RECRUITING` 鐘舵€佹椿鍔ㄥ彲瑙?- 琛ㄥ崟鍚畬鏁寸殑鏃堕棿璺ㄥ瓧娈垫牎楠岋細`寮€濮?< 缁撴潫`銆乣鎷涘嫙寮€濮?< 鎴 鈮?娲诲姩寮€濮媊

#### 鍙戝竷娲诲姩锛坄/admin/create`锛?- 鍒嗗尯甯冨眬锛氬乏渚?鍩烘湰淇℃伅 + 娲诲姩浠嬬粛"锛屽彸渚?鏃堕棿瀹夋帓"
- AI 鐢熸垚娲诲姩鎻忚堪锛氬～鍐欑被鍨嬪拰鍦扮偣鍚庤緭鍏ュ叧閿瘝鍗冲彲鐢熸垚锛堥渶閰嶇疆 `DEEPSEEK_API_KEY`锛?
#### 娲诲姩绛惧埌锛坄/admin/checkin`锛?- 浠呭垪鍑?*褰撳墠杩涜涓?*鐨勬椿鍔紙`start_time 鈮?鐜板湪 鈮?end_time`锛屼笖鏈彇娑堬級
- 灞曠ず鎶ュ悕鍚嶅崟锛岄€愪汉鏍囪绛惧埌锛屽凡绛惧埌璁板綍涓嶅彲閲嶅鎿嶄綔

#### 鏃堕暱鏍搁攢锛坄/admin/confirm`锛?- 浠呭垪鍑?*宸茬粨鏉熶絾灏氭湭缁撻」**鐨勬椿鍔紙`end_time < 鐜板湪`锛屾帓闄?CANCELLED 鍜?COMPLETED锛?- 灞曠ず"宸茬鍒颁笖鏈牳閿€"鐨勫彲鏍搁攢浜哄憳
- 鏍搁攢鍚庢樉绀?*鏍搁攢鏃堕棿**鍒?- > **鎺ㄨ崘宸ヤ綔娴?*锛氭椿鍔ㄧ粨鏉?鈫?鍦ㄦ妯″潡瀹屾垚鎵€鏈夋牳閿€ 鈫?鍥炲埌"娲诲姩绠＄悊"鐐瑰嚮**缁撻」**

#### 鏃堕暱鏌ヨ锛坄/admin/hours`锛?- 鏌ョ湅鎵€鏈夊織鎰胯€呯殑绱鏃堕暱鎺掕锛屾敮鎸佹寜濮撳悕/瀛﹀彿/鐢ㄦ埛鍚嶆ā绯婃悳绱?
---

## 馃攧 瀹屾暣涓氬姟娴佺▼

```
绠＄悊鍛樺彂甯冩椿鍔?    鈹?    鈻?鎷涘嫙寮€濮嬫椂闂村埌 鈫?蹇楁効鑰呭彲鎶ュ悕
    鈹?    鈻?鎶ュ悕鎴 鈫?娲诲姩寮€濮?鈫?绠＄悊鍛樺湪銆屾椿鍔ㄧ鍒般€嶆ā鍧楁爣璁扮鍒?    鈹?    鈻?娲诲姩缁撴潫 鈫?绠＄悊鍛樺湪銆屾椂闀挎牳閿€銆嶆ā鍧楅€愪汉鏍搁攢鏃堕暱
    鈹?    鈻?鍏ㄩ儴鏍搁攢瀹屾瘯 鈫?绠＄悊鍛樺湪銆屾椿鍔ㄧ鐞嗐€嶇偣鍑汇€岀粨椤广€?    鈹?    鈻?娲诲姩鍙樹负 COMPLETED锛屼粠鏍搁攢鍒楄〃娑堝け锛屽湪绠＄悊鍒楄〃浠ョ豢鑹查珮浜樉绀?```

---

## 馃悰 甯歌闂鎺掓煡

### 闂 1锛氭湇鍔″惎鍔ㄥけ璐?
**鎺掓煡姝ラ**锛?```bash
# Windows - 妫€鏌ョ鍙ｅ崰鐢?netstat -ano | findstr :8100
netstat -ano | findstr :9000

# Linux/Mac
lsof -i :8100
```

### 闂 2锛氭湇鍔℃棤娉曟敞鍐屽埌 Nacos锛坄serverAddr='null'` / `Client not connected`锛?
**瑙ｅ喅鏂规**锛?1. 鍏堝惎鍔?Nacos锛屽啀鍚姩鍚勫井鏈嶅姟锛圢acos 2.x 浣跨敤 8848 + 9848 绔彛锛夈€?2. 纭閰嶇疆涓寘鍚細
```properties
spring.cloud.nacos.server-addr=127.0.0.1:8848
spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
spring.cloud.nacos.discovery.fail-fast=false
```
3. 鑻ユ棩蹇椾粛鏄剧ず `serverAddr=null`锛屾鏌ョ郴缁?IDE 鏄惁瀛樺湪瑕嗙洊閰嶇疆鐨勭┖鐜鍙橀噺锛堝 `SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR`锛夛紝鍒犻櫎鎴栨敼涓?`127.0.0.1:8848`銆?
### 闂 3锛氬墠绔櫥褰?503 閿欒

**瑙ｅ喅鏂规**锛?1. 纭 `user-service` 鍜?`gateway-service` 閮藉凡姝ｅ父鍚姩銆?2. 鍦?Nacos 鎺у埗鍙扮‘璁?`user-service` 宸叉敞鍐屻€?3. 纭 `gateway-service` 鐨?pom.xml 涓湁 `spring-cloud-starter-loadbalancer` 渚濊禆銆?4. 鎸夐『搴忛噸鍚細`user-service` 鈫?`gateway-service`銆?
### 闂 4锛欳ORS 璺ㄥ煙閿欒

妫€鏌?`gateway-service/application.properties` 涓槸鍚︿娇鐢ㄤ簡 `allowed-origin-patterns`锛堣€岄潪 `allowed-origins`锛夛細
```properties
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-origin-patterns=*
spring.cloud.gateway.globalcors.cors-configurations.[/**].allow-credentials=true
```

### 闂 5锛歁yBatis-Plus 鍚姩鎶ラ敊

纭浣跨敤 Spring Boot 3 涓撶増骞朵笖 `common` 妯″潡閰嶇疆浜?`<skip>true</skip>`锛?```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.9</version>
</dependency>
```

### 闂 6锛氬墠绔棤娉曞姞杞?
```bash
# 娓呴櫎渚濊禆閲嶈
rm -rf node_modules package-lock.json
npm install
npm run dev
```

妫€鏌?Node.js 鐗堟湰锛堥渶 16+锛夛細`node -v`

---

## 馃搳 鏈嶅姟绔彛涓€瑙?
| 鏈嶅姟 | 绔彛 | 璁块棶鍦板潃 | 璇存槑 |
|------|------|---------|------|
| Nacos | 8848 | http://localhost:8848/nacos | 鏈嶅姟娉ㄥ唽涓績 |
| user-service | 8100 | 鈥?| 鐢ㄦ埛涓庤璇佹湇鍔?|
| activity-service | 8200 | 鈥?| 娲诲姩涓庢姤鍚嶆湇鍔?|
| gateway-service | 9000 | http://localhost:9000 | API 缃戝叧锛堢粺涓€鍏ュ彛锛?|
| monitor-service | 9100 | http://localhost:9100 | Spring Boot Admin 鐩戞帶 |
| frontend | 3000 | http://localhost:3000 | Vue 3 鍓嶇 |

---

## 馃摑 鍚姩妫€鏌ユ竻鍗?
- [ ] MySQL 宸插惎鍔ㄥ苟鎵ц `database/init.sql`
- [ ] Redis 宸插惎鍔紙`redis-cli ping` 杩斿洖 PONG锛?- [ ] Nacos 宸插惎鍔紝鎺у埗鍙板彲璁块棶
- [ ] `user-service`銆乣activity-service`銆乣gateway-service` 宸插惎鍔ㄥ苟娉ㄥ唽鍒?Nacos
- [ ] 鍓嶇 `npm run dev` 姝ｅ父杩愯
- [ ] 浣跨敤 `admin / password123` 鑳芥垚鍔熺櫥褰?- [ ] 棣栭〉"鏈€鏂版椿鍔?鍖哄煙鏄剧ず鎷涘嫙涓殑娲诲姩锛堟寜鎴鏃ュ崌搴忥級
- [ ] 绠＄悊鍚庡彴鑳芥煡鐪?20 鏉℃椿鍔ㄦ暟鎹?
鍏ㄩ儴瀹屾垚锛熸伃鍠滐紒馃帀 鐜板湪鍙互寮€濮嬫帰绱㈢郴缁熺殑鍚勯」鍔熻兘浜嗭紒

---

## 馃挕 寮€鍙戞彁绀?
1. **IDEA Run Dashboard**锛氬悓鏃剁鐞嗗涓?Spring Boot 鏈嶅姟锛屾帹鑽愪娇鐢?2. **鍝嶅簲寮忔祴璇?*锛欶12 鈫?璁惧妯℃嫙鍣紝鍙獙璇佺Щ鍔ㄧ甯冨眬
3. **鏁版嵁瀵硅处**锛歚v_activity_statistics` 瑙嗗浘鍙敤浜庢暟鎹簱灞傞潰鏍告煡鍚勬椿鍔ㄧ殑瀹為檯鎶ュ悕/绛惧埌/鏍搁攢鏁?4. **Redis Key**锛氭椿鍔ㄥ悕棰濅互 `activity:stock:{id}` 涓?key 缂撳瓨锛屽彲鐢?`redis-cli keys "activity:*"` 鏌ョ湅

---

馃挰 閬囧埌闂锛熸煡鐪?[甯歌闂](#-甯歌闂鎺掓煡) 鎴栨彁浜?Issue銆?
