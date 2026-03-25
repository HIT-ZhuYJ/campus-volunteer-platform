# 椤圭洰鐩綍缁撴瀯

```
cloud-demo/                                    # 椤圭洰鏍圭洰褰?鈹?鈹溾攢鈹€ .idea/                                     # IDEA閰嶇疆鐩綍锛堣嚜鍔ㄧ敓鎴愶級
鈹溾攢鈹€ .gitignore                                 # Git蹇界暐閰嶇疆
鈹?鈹溾攢鈹€ pom.xml                                    # 鐖禤OM鏂囦欢
鈹?  鈹斺攢鈹€ Spring Boot 3.3.4
鈹?  鈹斺攢鈹€ Spring Cloud Alibaba 2023.0.3.2
鈹?鈹溾攢鈹€ database/                                  # 鏁版嵁搴撹剼鏈洰褰?鈹?  鈹斺攢鈹€ init.sql                              # 鍞竴鍏ㄩ噺鍒濆鍖栬剼鏈紙绾?446 琛岋級
鈹?      鈹溾攢鈹€ DROP/CREATE volunteer_platform
鈹?      鈹溾攢鈹€ sys_user锛堢敤鎴疯〃锛?1 鏉℃祴璇曟暟鎹級
鈹?      鈹溾攢鈹€ vol_activity锛堟椿鍔ㄨ〃锛?0 鏉★紝鍚?registration_start_time/registration_deadline/idx_end_time 绱㈠紩锛?鈹?      鈹溾攢鈹€ vol_registration锛堟姤鍚嶈〃锛?4 鏉★紝鍚?confirm_time 瀛楁锛?鈹?      鈹斺攢鈹€ v_activity_statistics锛堢粺璁¤鍥撅紝BI 鐢ㄩ€旓級
鈹?鈹溾攢鈹€ logs/                                      # 鏃ュ織鐩綍
鈹?  鈹溾攢鈹€ monitor-service.log
鈹?  鈹溾攢鈹€ gateway-service.log
鈹?  鈹溾攢鈹€ user-service.log
鈹?  鈹斺攢鈹€ activity-service.log
鈹?鈹溾攢鈹€ services/                                  # 寰湇鍔℃ā鍧楃洰褰?鈹?  鈹?鈹?  鈹溾攢鈹€ pom.xml                               # services鐖禤OM
鈹?  鈹?鈹?  鈹溾攢鈹€ common/                               # 銆愬叕鍏辨ā鍧椼€?鈹?  鈹?  鈹溾攢鈹€ pom.xml
鈹?  鈹?  鈹斺攢鈹€ src/main/java/org/example/common/
鈹?  鈹?      鈹溾攢鈹€ result/
鈹?  鈹?      鈹?  鈹斺攢鈹€ Result.java               # 缁熶竴杩斿洖缁撴灉灏佽
鈹?  鈹?      鈹溾攢鈹€ util/
鈹?  鈹?      鈹?  鈹斺攢鈹€ JwtUtil.java              # JWT宸ュ叿绫?鈹?  鈹?      鈹溾攢鈹€ exception/
鈹?  鈹?      鈹?  鈹斺攢鈹€ BusinessException.java    # 涓氬姟寮傚父
鈹?  鈹?      鈹斺攢鈹€ constant/
鈹?  鈹?          鈹斺攢鈹€ RedisKeyConstant.java     # Redis Key甯搁噺
鈹?  鈹?鈹?  鈹溾攢鈹€ gateway-service/                      # 銆愮綉鍏虫湇鍔°€戠鍙? 9000
鈹?  鈹?  鈹溾攢鈹€ pom.xml
鈹?  鈹?  鈹?  鈹溾攢鈹€ spring-cloud-starter-gateway
鈹?  鈹?  鈹?  鈹溾攢鈹€ nacos-discovery
鈹?  鈹?  鈹?  鈹斺攢鈹€ common妯″潡
鈹?  鈹?  鈹斺攢鈹€ src/main/
鈹?  鈹?      鈹溾攢鈹€ java/org/example/
鈹?  鈹?      鈹?  鈹溾攢鈹€ GatewayApplication.java   # 鍚姩绫?鈹?  鈹?      鈹?  鈹溾攢鈹€ filter/
鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ AuthFilter.java       # JWT璁よ瘉杩囨护鍣?鈹?  鈹?      鈹?  鈹斺攢鈹€ config/
鈹?  鈹?      鈹?      鈹斺攢鈹€ CorsConfig.java       # 璺ㄥ煙閰嶇疆
鈹?  鈹?      鈹斺攢鈹€ resources/
鈹?  鈹?          鈹斺攢鈹€ application.properties     # 璺敱閰嶇疆
鈹?  鈹?              鈹溾攢鈹€ server.port=9000
鈹?  鈹?              鈹溾攢鈹€ 璺敱瑙勫垯閰嶇疆
鈹?  鈹?              鈹斺攢鈹€ JWT閰嶇疆
鈹?  鈹?鈹?  鈹溾攢鈹€ user-service/                         # 銆愮敤鎴锋湇鍔°€戠鍙? 8100
鈹?  鈹?  鈹溾攢鈹€ pom.xml
鈹?  鈹?  鈹?  鈹溾攢鈹€ spring-boot-starter-web
鈹?  鈹?  鈹?  鈹溾攢鈹€ mybatis-plus
鈹?  鈹?  鈹?  鈹溾攢鈹€ mysql-connector
鈹?  鈹?  鈹?  鈹斺攢鈹€ common妯″潡
鈹?  鈹?  鈹斺攢鈹€ src/main/
鈹?  鈹?      鈹溾攢鈹€ java/org/example/
鈹?  鈹?      鈹?  鈹溾攢鈹€ UserApplication.java      # 鍚姩绫?鈹?  鈹?      鈹?  鈹溾攢鈹€ entity/
鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ User.java             # 鐢ㄦ埛瀹炰綋
鈹?  鈹?      鈹?  鈹溾攢鈹€ dto/
鈹?  鈹?      鈹?  鈹?  鈹溾攢鈹€ LoginRequest.java     # 鐧诲綍璇锋眰DTO
鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ RegisterRequest.java  # 娉ㄥ唽璇锋眰DTO
鈹?  鈹?      鈹?  鈹溾攢鈹€ vo/
鈹?  鈹?      鈹?  鈹?  鈹溾攢鈹€ LoginResponse.java    # 鐧诲綍鍝嶅簲VO
鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ UserInfo.java         # 鐢ㄦ埛淇℃伅VO
鈹?  鈹?      鈹?  鈹溾攢鈹€ mapper/
鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ UserMapper.java       # MyBatis Mapper
鈹?  鈹?      鈹?  鈹溾攢鈹€ service/
鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ UserService.java      # 涓氬姟閫昏緫灞?鈹?  鈹?      鈹?  鈹溾攢鈹€ controller/
鈹?  鈹?      鈹?  鈹?  鈹溾攢鈹€ UserController.java           # 鐢ㄦ埛鎺у埗鍣?鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ InternalUserController.java   # 鍐呴儴璋冪敤鎺ュ彛
鈹?  鈹?      鈹?  鈹斺攢鈹€ exception/
鈹?  鈹?      鈹?      鈹斺攢鈹€ GlobalExceptionHandler.java   # 鍏ㄥ眬寮傚父澶勭悊
鈹?  鈹?      鈹斺攢鈹€ resources/
鈹?  鈹?          鈹斺攢鈹€ application.properties     # 閰嶇疆鏂囦欢
鈹?  鈹?              鈹溾攢鈹€ server.port=8100
鈹?  鈹?              鈹溾攢鈹€ MySQL閰嶇疆
鈹?  鈹?              鈹溾攢鈹€ MyBatis-Plus閰嶇疆
鈹?  鈹?              鈹斺攢鈹€ Nacos閰嶇疆
鈹?  鈹?鈹?  鈹溾攢鈹€ activity-service/                     # 銆愭椿鍔ㄦ湇鍔°€戠鍙? 8200
鈹?  鈹?  鈹溾攢鈹€ pom.xml
鈹?  鈹?  鈹?  鈹溾攢鈹€ spring-boot-starter-web
鈹?  鈹?  鈹?  鈹溾攢鈹€ mybatis-plus
鈹?  鈹?  鈹?  鈹溾攢鈹€ mysql-connector
鈹?  鈹?  鈹?  鈹溾攢鈹€ spring-boot-starter-data-redis
鈹?  鈹?  鈹?  鈹溾攢鈹€ spring-cloud-starter-openfeign
鈹?  鈹?  鈹?  鈹斺攢鈹€ common妯″潡
鈹?  鈹?  鈹斺攢鈹€ src/main/
鈹?  鈹?      鈹溾攢鈹€ java/org/example/
鈹?  鈹?      鈹?  鈹溾攢鈹€ ActivityApplication.java   # 鍚姩绫?鈹?  鈹?      鈹?  鈹溾攢鈹€ entity/
鈹?  鈹?      鈹?  鈹?  鈹溾攢鈹€ Activity.java          # 娲诲姩瀹炰綋
鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ Registration.java      # 鎶ュ悕瀹炰綋
鈹?  鈹?      鈹?  鈹溾攢鈹€ dto/
鈹?  鈹?      鈹?  鈹?  鈹溾攢鈹€ ActivityCreateRequest.java  # 鍒涘缓娲诲姩DTO
鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ AIGenerateRequest.java      # AI鐢熸垚璇锋眰DTO
鈹?  鈹?      鈹?  鈹溾攢鈹€ vo/
鈹?  鈹?      鈹?  鈹?  鈹溾攢鈹€ ActivityVO.java        # 娲诲姩VO
鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ RegistrationVO.java    # 鎶ュ悕VO锛堝惈 confirmTime 瀛楁锛?鈹?  鈹?      鈹?  鈹溾攢鈹€ mapper/
鈹?  鈹?      鈹?  鈹?  鈹溾攢鈹€ ActivityMapper.java    # 娲诲姩Mapper
鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ RegistrationMapper.java # 鎶ュ悕Mapper
鈹?  鈹?      鈹?  鈹溾攢鈹€ service/
鈹?  鈹?      鈹?  鈹?  鈹溾攢鈹€ ActivityService.java          # 鏍稿績涓氬姟閫昏緫锛堝惈 COMPLETED 杩囨护銆佹嫑鍕熸埅姝㈡帓搴忥級
鈹?  鈹?      鈹?  鈹?  鈹溾攢鈹€ ActivityScheduleValidator.java # 鏃堕棿鍚堟硶鎬ф牎楠?鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ AIService.java                # AI鏈嶅姟
鈹?  鈹?      鈹?  鈹溾攢鈹€ feign/
鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ UserServiceClient.java # Feign瀹㈡埛绔?鈹?  鈹?      鈹?  鈹溾攢鈹€ controller/
鈹?  鈹?      鈹?  鈹?  鈹斺攢鈹€ ActivityController.java # 娲诲姩鎺у埗鍣?鈹?  鈹?      鈹?  鈹斺攢鈹€ exception/
鈹?  鈹?      鈹?      鈹斺攢鈹€ GlobalExceptionHandler.java
鈹?  鈹?      鈹斺攢鈹€ resources/
鈹?  鈹?          鈹斺攢鈹€ application.properties      # 閰嶇疆鏂囦欢
鈹?  鈹?              鈹溾攢鈹€ server.port=8200
鈹?  鈹?              鈹溾攢鈹€ MySQL閰嶇疆
鈹?  鈹?              鈹溾攢鈹€ Redis閰嶇疆
鈹?  鈹?              鈹溾攢鈹€ DeepSeek锛圤penAI 鍏煎锛堿PI 閰嶇疆锛孌EEPSEEK_API_KEY
鈹?  鈹?              鈹斺攢鈹€ Nacos閰嶇疆
鈹?  鈹?鈹?  鈹溾攢鈹€ monitor-service/                      # 銆愮洃鎺ф湇鍔°€戠鍙? 9100
鈹?  鈹?  鈹溾攢鈹€ pom.xml
鈹?  鈹?  鈹?  鈹溾攢鈹€ spring-boot-admin-starter-server
鈹?  鈹?  鈹?  鈹溾攢鈹€ spring-boot-starter-web
鈹?  鈹?  鈹?  鈹斺攢鈹€ nacos-discovery
鈹?  鈹?  鈹斺攢鈹€ src/main/
鈹?  鈹?      鈹溾攢鈹€ java/org/example/
鈹?  鈹?      鈹?  鈹斺攢鈹€ MonitorApplication.java    # 鍚姩绫?鈹?  鈹?      鈹斺攢鈹€ resources/
鈹?  鈹?          鈹斺攢鈹€ application.properties      # 閰嶇疆鏂囦欢
鈹?  鈹?              鈹溾攢鈹€ server.port=9100
鈹?  鈹?              鈹斺攢鈹€ Spring Boot Admin閰嶇疆
鈹?鈹溾攢鈹€ frontend/                                  # 鍓嶇椤圭洰
鈹?  鈹斺攢鈹€ src/
鈹?      鈹溾攢鈹€ views/
鈹?      鈹?  鈹溾攢鈹€ Login.vue                     # 鐧诲綍锛堝揩閫熺櫥褰曟爣绛撅級
鈹?      鈹?  鈹溾攢鈹€ Register.vue                  # 娉ㄥ唽锛堝崱鐗囧紡缇庡寲锛?鈹?      鈹?  鈹溾攢鈹€ Home.vue                      # 棣栭〉锛堜粎灞曠ず鎷涘嫙涓椿鍔紝宸茬鍒扮粺璁★級
鈹?      鈹?  鈹溾攢鈹€ ActivityList.vue              # 娲诲姩鍒楄〃锛堣兌鍥婄瓫閫夛紝鎸夋埅姝㈡椂闂村崌搴忥級
鈹?      鈹?  鈹溾攢鈹€ ActivityDetail.vue            # 娲诲姩璇︽儏锛圚ero 妯箙 + 鍙屾爮甯冨眬锛?鈹?      鈹?  鈹溾攢鈹€ MyCenter.vue                  # 涓汉涓績锛堟笎鍙樺崱鐗囷紝鍥涚淮缁熻锛?鈹?      鈹?  鈹斺攢鈹€ admin/
鈹?      鈹?      鈹溾攢鈹€ AdminLayout.vue           # 绠＄悊鍚庡彴甯冨眬
鈹?      鈹?      鈹溾攢鈹€ ActivityManage.vue        # 娲诲姩绠＄悊锛堢粨椤?鍙栨秷楂樹寒琛岋級
鈹?      鈹?      鈹溾攢鈹€ ActivityCreate.vue        # 鍙戝竷娲诲姩锛堝垎缁勫崱鐗囪〃鍗曪級
鈹?      鈹?      鈹溾攢鈹€ ActivityCheckIn.vue       # 娲诲姩绛惧埌绠＄悊
鈹?      鈹?      鈹溾攢鈹€ VolunteerHours.vue        # 蹇楁効鏃堕暱缁熻
鈹?      鈹?      鈹斺攢鈹€ HoursConfirm.vue          # 鏃堕暱鏍搁攢锛堜粎宸茬粨鏉熸湭缁撻」娲诲姩锛?鈹?      鈹溾攢鈹€ utils/
鈹?      鈹?  鈹溾攢鈹€ activityPhase.js              # 娲诲姩闃舵鏍囩锛堝惈 COMPLETED 鐙珛澶勭悊锛?鈹?      鈹?  鈹斺攢鈹€ recruitment.js               # 鎷涘嫙鐘舵€佸伐鍏?鈹?      鈹斺攢鈹€ api/
鈹?          鈹溾攢鈹€ activity.js                   # 娲诲姩鐩稿叧 API
鈹?          鈹斺攢鈹€ user.js                       # 鐢ㄦ埛鐩稿叧 API
鈹?鈹溾攢鈹€ 馃搫 鏂囨。涓?CI锛堜粨搴撴牴鐩綍锛?鈹溾攢鈹€ README.md
鈹溾攢鈹€ ARCHITECTURE.md
鈹溾攢鈹€ DEPLOY.md
鈹溾攢鈹€ API_TEST.md
鈹溾攢鈹€ QUICKSTART.md
鈹溾攢鈹€ PROJECT_SUMMARY.md
鈹溾攢鈹€ CHECKLIST.md
鈹溾攢鈹€ .github/workflows/                        # GitHub Actions 宸ヤ綔娴?鈹?鈹斺攢鈹€ 馃洜锔?宸ュ叿鑴氭湰
    鈹溾攢鈹€ start-all.bat                         # Windows 鍚姩锛堥『搴忥細monitor鈫抔ateway鈫抲ser鈫抋ctivity锛?    鈹斺攢鈹€ start-all.sh                          # Linux 鍚姩锛堝悓涓婏級
```

## 鍏抽敭鐩綍璇存槑

### 1. services/common/ - 鍏叡妯″潡
鎵€鏈夊井鏈嶅姟鍏变韩鐨勪唬鐮侊紝閬垮厤閲嶅锛?- 缁熶竴杩斿洖缁撴灉灏佽
- JWT宸ュ叿绫?- 涓氬姟寮傚父瀹氫箟
- Redis Key甯搁噺

### 2. services/gateway-service/ - 缃戝叧鏈嶅姟
绯荤粺鐨勭粺涓€鍏ュ彛锛?- 璺敱杞彂鍒板悇寰湇鍔?- JWT Token楠岃瘉
- 璺ㄥ煙CORS澶勭悊
- 璇锋眰澶存敞鍏ョ敤鎴蜂俊鎭?
### 3. services/user-service/ - 鐢ㄦ埛鏈嶅姟
鐢ㄦ埛鐩稿叧鐨勬墍鏈夊姛鑳斤細
- 鐢ㄦ埛娉ㄥ唽/鐧诲綍
- JWT Token鐢熸垚
- 鐢ㄦ埛淇℃伅绠＄悊
- 蹇楁効鏃堕暱绱

### 4. services/activity-service/ - 娲诲姩鏈嶅姟
鏍稿績涓氬姟鏈嶅姟锛?- 娲诲姩CRUD
- 鎶ュ悕绠＄悊锛圧edis闃茶秴鍗栵級
- 鏃堕暱鏍搁攢
- AI鏅鸿兘鐢熸垚鏂囨
- Feign璋冪敤鐢ㄦ埛鏈嶅姟

### 5. services/monitor-service/ - 鐩戞帶鏈嶅姟
鏈嶅姟鍋ュ悍鐩戞帶锛?- 鑷姩鍙戠幇Nacos鏈嶅姟
- JVM鎬ц兘鐩戞帶
- 瀹炴椂鏃ュ織鏌ョ湅
- HTTP璇锋眰杩借釜

## 鏂囦欢鏁伴噺缁熻

| 绫诲瀷 | 鏁伴噺 | 璇存槑 |
|------|------|------|
| Java婧愭枃浠?| 35 | services 涓嬩笟鍔′笌鍏叡浠ｇ爜锛堟柊澧?ActivityScheduleValidator锛?|
| POM鏂囦欢 | 7 | 鍖呮嫭鐖禤OM |
| 閰嶇疆鏂囦欢 | 5 | application.properties |
| SQL鑴氭湰 | 1 | init.sql锛?46 琛岋紝鍚?11 鐢ㄦ埛/20 娲诲姩/54 鎶ュ悕锛?|
| 鍓嶇 Vue 鏂囦欢 | 14 | 鍚?ActivityCheckIn銆乂olunteerHours |
| 鏂囨。鏂囦欢 | 10 | Markdown鏂囨。 |
| 鍚姩鑴氭湰 | 2 | bat + sh |

## 浠ｇ爜琛屾暟缁熻锛堜及绠楋級

| 妯″潡 | Java浠ｇ爜 | 閰嶇疆浠ｇ爜 | 鏂囨。 |
|------|----------|----------|------|
| common | ~200 琛?| - | - |
| gateway-service | ~150 琛?| ~30 琛?| - |
| user-service | ~350 琛?| ~40 琛?| - |
| activity-service | ~600 琛?| ~50 琛?| - |
| monitor-service | ~20 琛?| ~20 琛?| - |
| 鏁版嵁搴撹剼鏈?| - | ~140 琛?| init.sql 鍏ㄩ噺 |
| 鏂囨。 | - | - | ~2000 琛?|
| **鎬昏** | **~1320 琛?* | **~340 琛?* | **~2000 琛?* |

## 鏍稿績鏂囦欢娓呭崟

### 鍚姩绫?(5涓?
1. `GatewayApplication.java` - 缃戝叧鍚姩绫?2. `UserApplication.java` - 鐢ㄦ埛鏈嶅姟鍚姩绫?3. `ActivityApplication.java` - 娲诲姩鏈嶅姟鍚姩绫?4. `MonitorApplication.java` - 鐩戞帶鏈嶅姟鍚姩绫?5. (common妯″潡鏃犲惎鍔ㄧ被)

### Controller (4涓?
1. `UserController.java` - 3涓帴鍙?2. `InternalUserController.java` - 1涓唴閮ㄦ帴鍙?3. `ActivityController.java` - 娲诲姩/鎶ュ悕/鏍搁攢/AI 绛?REST 鎺ュ彛

### Service (4涓?
1. `UserService.java` - 鐢ㄦ埛涓氬姟閫昏緫
2. `ActivityService.java` - 娲诲姩涓氬姟閫昏緫
3. `AIService.java` - AI鏈嶅姟閫昏緫

### Mapper (3涓?
1. `UserMapper.java`
2. `ActivityMapper.java`
3. `RegistrationMapper.java`

### Entity (3涓?
1. `User.java`
2. `Activity.java`
3. `Registration.java`

### Filter/Config (2涓?
1. `AuthFilter.java` - JWT璁よ瘉杩囨护鍣?2. `CorsConfig.java` - 璺ㄥ煙閰嶇疆

## 绔彛鍒嗛厤

| 鏈嶅姟 | 绔彛 | 璇存槑 |
|------|------|------|
| Nacos | 8848 | 娉ㄥ唽涓績 |
| Redis | 6379 | 缂撳瓨 |
| MySQL | 3306 | 鏁版嵁搴?|
| gateway-service | 9000 | 缃戝叧 |
| user-service | 8100 | 鐢ㄦ埛鏈嶅姟 |
| activity-service | 8200 | 娲诲姩鏈嶅姟 |
| monitor-service | 9100 | 鐩戞帶涓績 |

## 渚濊禆鍏崇郴鍥?
```
鈹屸攢鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹?鈹? gateway-service鈹?鈹?   (9000)       鈹?鈹斺攢鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹攢鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹?         鈹?渚濊禆
         鈻?    鈹屸攢鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹?    鈹?common 鈹傗梽鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹?    鈹斺攢鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹?          鈹?         鈻?              鈹?         鈹?渚濊禆          鈹?渚濊禆
鈹屸攢鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹粹攢鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹? 鈹屸攢鈹€鈹€鈹粹攢鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹?鈹? user-service   鈹? 鈹?activity-service 鈹?鈹?   (8100)       鈹傗梽鈹€鈹?   (8200)        鈹?鈹斺攢鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹? 鈹斺攢鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹?                         Feign璋冪敤

    鈹屸攢鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹?    鈹?monitor-service  鈹?    鈹?   (9100)        鈹?    鈹斺攢鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹?         鐙珛鐩戞帶
```

## 蹇€熷鑸?
- **鏌ョ湅椤圭洰浠嬬粛**: `README.md`
- **浜嗚В鏋舵瀯璁捐**: `ARCHITECTURE.md`
- **瀛︿範閮ㄧ讲姝ラ**: `DEPLOY.md`
- **娴嬭瘯API鎺ュ彛**: `API_TEST.md`
- **蹇€熷惎鍔?*: `QUICKSTART.md`
- **椤圭洰鎬荤粨**: `PROJECT_SUMMARY.md`

