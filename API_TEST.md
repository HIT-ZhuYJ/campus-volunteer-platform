# 馃摗 API 鎺ュ彛娴嬭瘯鏂囨。

鏈枃妗ｆ彁渚涙墍鏈夊悗绔?API 鎺ュ彛鐨勮缁嗚鏄庡拰娴嬭瘯绀轰緥銆?
## 馃寪 鍩虹淇℃伅

- **API缃戝叧鍦板潃**锛歚http://localhost:9000`
- **璁よ瘉鏂瑰紡**锛欽WT Bearer Token锛堢綉鍏?`AuthFilter` 鐧藉悕鍗曢櫎澶栵級
- **缃戝叧鐧藉悕鍗曪紙鏃犻渶 Token锛?*锛歚/user/login`銆乣/user/register`銆佷互鍙婁互 `/activity/list` 寮€澶寸殑璺緞锛堟椿鍔ㄥ垪琛ㄥ叕寮€锛?- **杩斿洖鏍煎紡**锛欽SON

### 缁熶竴杩斿洖鏍煎紡

```json
{
  "code": 200,
  "message": "鎴愬姛",
  "data": { ... }
}
```

| 鐘舵€佺爜 | 璇存槑 |
|--------|------|
| 200 | 鎴愬姛 |
| 400 | 璇锋眰鍙傛暟閿欒 |
| 401 | 鏈璇佹垨Token杩囨湡 |
| 403 | 鏉冮檺涓嶈冻 |
| 500 | 鏈嶅姟鍣ㄥ唴閮ㄩ敊璇?|

## 馃攼 鐢ㄦ埛鏈嶅姟 (user-service)

### 1. 鐢ㄦ埛娉ㄥ唽

**鎺ュ彛**锛歚POST /user/register`

**鏃犻渶璁よ瘉**

**璇锋眰绀轰緥**锛?```bash
curl -X POST http://localhost:9000/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "realName": "娴嬭瘯鐢ㄦ埛",
    "studentNo": "2024999",
    "phone": "13800138888",
    "email": "testuser@university.edu"
  }'
```

**璇锋眰鍙傛暟**锛?```json
{
  "username": "string",       // 鐢ㄦ埛鍚嶏紝蹇呭～锛屽敮涓€
  "password": "string",       // 瀵嗙爜锛屽繀濉紝6浣嶄互涓?  "realName": "string",       // 鐪熷疄濮撳悕锛屽繀濉?  "studentNo": "string",      // 瀛﹀彿锛屽繀濉紝鍞竴
  "phone": "string",          // 鎵嬫満鍙凤紝閫夊～
  "email": "string"           // 閭锛岄€夊～
}
```

**鍝嶅簲绀轰緥**锛?```json
{
  "code": 200,
  "message": "娉ㄥ唽鎴愬姛",
  "data": null
}
```

### 2. 鐢ㄦ埛鐧诲綍

**鎺ュ彛**锛歚POST /user/login`

**鏃犻渶璁よ瘉**

**璇锋眰绀轰緥**锛?```bash
curl -X POST http://localhost:9000/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

**璇锋眰鍙傛暟**锛?```json
{
  "username": "string",  // 鐢ㄦ埛鍚嶏紝蹇呭～
  "password": "string"   // 瀵嗙爜锛屽繀濉?}
```

**鍝嶅簲绀轰緥**锛?```json
{
  "code": 200,
  "message": "鐧诲綍鎴愬姛",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiQURNSU4iLCJleHAiOjE3MTIwMDAwMDB9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "绠＄悊鍛?,
      "studentNo": "2021001",
      "phone": "13800138000",
      "email": "admin@university.edu",
      "role": "ADMIN",
      "totalVolunteerHours": 0.00
    }
  }
}
```

### 3. 鑾峰彇褰撳墠鐢ㄦ埛淇℃伅

**鎺ュ彛**锛歚GET /user/info`

**闇€瑕佽璇?*

**璇锋眰绀轰緥**锛?```bash
curl -X GET http://localhost:9000/user/info \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**鍝嶅簲绀轰緥**锛?```json
{
  "code": 200,
  "message": "鎴愬姛",
  "data": {
    "id": 2,
    "username": "student01",
    "realName": "寮犱笁",
    "studentNo": "2021101",
    "phone": "13800138001",
    "email": "zhangsan@university.edu",
    "role": "VOLUNTEER",
    "totalVolunteerHours": 12.50
  }
}
```

### 4. 鏇存柊蹇楁効鏃堕暱锛堝唴閮ㄦ帴鍙ｏ級

**鎺ュ彛**锛歚POST /user/internal/updateHours/{userId}?hours={hours}`

**浠呮湇鍔￠棿璋冪敤**

**璇存槑**锛氭鎺ュ彛鐢?activity-service 閫氳繃 Feign 璋冪敤锛岀敤浜庢牳閿€鏃堕暱鏃舵洿鏂扮敤鎴风疮璁℃椂闀裤€?
## 馃幆 娲诲姩鏈嶅姟 (activity-service)

### 1. 娲诲姩鍒楄〃锛堝叕寮€锛?
**鎺ュ彛**锛歚GET /activity/list`

**鏃犻渶璁よ瘉**

**璇锋眰鍙傛暟**锛?- `page`锛氶〉鐮侊紙榛樿1锛?- `size`锛氭瘡椤靛ぇ灏忥紙榛樿10锛?- `status`锛氭椿鍔ㄧ姸鎬侊紙鍙€夛細`RECRUITING`/`COMPLETED`/`CANCELLED`锛屾敞锛歚ONGOING` 涓嶆槸瀛樺偍鐘舵€侊紝璇峰嬁浣跨敤锛?- `category`锛氭椿鍔ㄧ被鍨嬶紙鍙€夛細瀛﹂暱鐏偓銆佷功璁伴┛绔欑瓑锛?- `recruitmentPhase`锛氭嫑鍕熼樁娈碉紙鍙€夛紝涓庡墠绔€屾嫑鍕熺姸鎬併€嶇瓫閫変竴鑷达級
  - `NOT_STARTED`锛氬綋鍓嶆椂闂存棭浜?`registrationStartTime`
  - `RECRUITING`锛氬湪鎷涘嫙绐楀彛鍐呬笖闈炵粨椤?鍙栨秷
  - `ENDED`锛氬凡杩囨姤鍚嶆埅姝㈡垨娲诲姩涓?`COMPLETED`/`CANCELLED`

**鎺掑簭**锛氶粯璁ゆ寜 `registration_deadline` 鍗囧簭鎺掑垪锛堟埅姝㈡椂闂存渶杩戠殑鎺掓渶鍓嶏級

**璇锋眰绀轰緥**锛?```bash
# 鑾峰彇绗?椤碉紝姣忛〉10鏉?curl "http://localhost:9000/activity/list?page=1&size=10"

# 绛涢€夋嫑鍕熶腑鐨勬椿鍔?curl "http://localhost:9000/activity/list?status=RECRUITING"

# 绛涢€夊闀跨伀鐐被娲诲姩
curl "http://localhost:9000/activity/list?category=瀛﹂暱鐏偓"

# 鎸夋嫑鍕熼樁娈碉紙涓?init.sql 绀轰緥鏁版嵁銆佸綋鍓嶆棩鏈熺粍鍚堜娇鐢級
curl "http://localhost:9000/activity/list?recruitmentPhase=ENDED"
```

**鍝嶅簲璇存槑**锛歚data` 涓?MyBatis-Plus 鍒嗛〉瀵硅薄锛屼富瑕佸瓧娈典负 **`records`**锛堝垪琛級銆?*`total`**銆?*`size`**銆?*`current`**銆?*`pages`**銆?
**鍝嶅簲绀轰緥**锛?```json
{
  "code": 200,
  "message": "鎿嶄綔鎴愬姛",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "瀛﹂暱鐏偓 - 鏂扮敓鍏ュ寮曞",
        "description": "鍗忓姪鏂扮敓鍔炵悊鍏ュ鎵嬬画锛岃В绛旂枒闂?..",
        "location": "瀛︽牎涓滈棬杩庢柊鐐?,
        "maxParticipants": 50,
        "currentParticipants": 2,
        "volunteerHours": 4.0,
        "startTime": "2026-09-01T08:00:00",
        "endTime": "2026-09-01T18:00:00",
        "registrationStartTime": "2026-08-01T00:00:00",
        "registrationDeadline": "2026-08-25T23:59:59",
        "status": "RECRUITING",
        "category": "瀛﹂暱鐏偓",
        "isRegistered": false,
        "availableSlots": 48
      }
    ],
    "total": 20,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 娲诲姩璇︽儏

**鎺ュ彛**锛歚GET /activity/{id}`

**闇€瑕佽璇?*

**璇锋眰绀轰緥**锛?```bash
curl -X GET http://localhost:9000/activity/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**鍝嶅簲绀轰緥**锛?```json
{
  "code": 200,
  "message": "鎿嶄綔鎴愬姛",
  "data": {
    "id": 1,
    "title": "瀛﹂暱鐏偓 - 鏂扮敓鍏ュ寮曞",
    "description": "鍗忓姪鏂扮敓鍔炵悊鍏ュ鎵嬬画锛岃В绛旂枒闂紝甯姪鏂扮敓灏藉揩閫傚簲澶у鐢熸椿...",
    "location": "瀛︽牎涓滈棬杩庢柊鐐?,
    "maxParticipants": 50,
    "currentParticipants": 2,
    "volunteerHours": 4.0,
    "startTime": "2026-09-01T08:00:00",
    "endTime": "2026-09-01T18:00:00",
    "registrationStartTime": "2026-08-01T00:00:00",
    "registrationDeadline": "2026-08-25T23:59:59",
    "status": "RECRUITING",
    "category": "瀛﹂暱鐏偓",
    "isRegistered": false,
    "availableSlots": 48
  }
}
```

### 3. 鍒涘缓娲诲姩锛堢鐞嗗憳锛?
**鎺ュ彛**锛歚POST /activity/create`

**闇€瑕佺鐞嗗憳鏉冮檺**

**璇锋眰绀轰緥**锛?```bash
curl -X POST http://localhost:9000/activity/create \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "涔﹁椹跨珯 - 鍛ㄦ湯鍊肩彮",
    "description": "鍦ㄤ功璁伴┛绔欏€肩彮锛屼负鍚屽浠彁渚涘挩璇㈡湇鍔?,
    "location": "鍥句功棣嗕竴妤间功璁伴┛绔?,
    "maxParticipants": 10,
    "volunteerHours": 3.0,
    "startTime": "2026-09-15T09:00:00",
    "endTime": "2026-09-15T17:00:00",
    "registrationStartTime": "2026-09-01T00:00:00",
    "registrationDeadline": "2026-09-10T23:59:59",
    "category": "涔﹁椹跨珯"
  }'
```

**璇锋眰鍙傛暟**锛?```json
{
  "title": "string",
  "description": "string",
  "location": "string",
  "maxParticipants": 10,
  "volunteerHours": 3.0,
  "startTime": "2026-09-15T09:00:00",
  "endTime": "2026-09-15T17:00:00",
  "registrationStartTime": "2026-09-01T00:00:00",
  "registrationDeadline": "2026-09-10T23:59:59",
  "category": "string"
}
```

**鏍￠獙瑙勫垯锛堝悗绔?`ActivityScheduleValidator`锛?*锛?- `registrationStartTime` < `registrationDeadline` 鈮?`startTime` < `endTime`
- `startTime` 蹇呴』鏃╀簬 `endTime`锛堝悗绔細杩斿洖 400 + 鍏蜂綋閿欒淇℃伅锛?
**鍝嶅簲绀轰緥**锛?```json
{
  "code": 200,
  "message": "娲诲姩鍒涘缓鎴愬姛",
  "data": null
}
```

### 4. 鎶ュ悕娲诲姩

**鎺ュ彛**锛歚POST /activity/register/{activityId}`

**闇€瑕佽璇?*

**璇锋眰绀轰緥**锛?```bash
curl -X POST http://localhost:9000/activity/register/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**鍝嶅簲绀轰緥**锛?```json
{
  "code": 200,
  "message": "鎶ュ悕鎴愬姛",
  "data": null
}
```

**鍙兘鐨勯敊璇?*锛?- `400`锛氬悕棰濆凡婊?- `400`锛氬凡鎶ュ悕杩囪娲诲姩
- `400`锛氭姤鍚嶆埅姝㈡椂闂村凡杩?
### 5. 鎴戠殑鎶ュ悕璁板綍

**鎺ュ彛**锛歚GET /activity/myRegistrations`

**闇€瑕佽璇?*

**璇锋眰绀轰緥**锛?```bash
curl -X GET http://localhost:9000/activity/myRegistrations \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**鍝嶅簲绀轰緥**锛?```json
{
  "code": 200,
  "message": "鎴愬姛",
  "data": [
    {
      "id": 1,
      "activityId": 1,
      "activityTitle": "瀛﹂暱鐏偓 - 鏂扮敓鍏ュ寮曞",
      "location": "瀛︽牎涓滈棬杩庢柊鐐?,
      "volunteerHours": 4.0,
      "startTime": "2024-09-01T08:00:00",
      "endTime": "2024-09-01T18:00:00",
      "status": "RECRUITING",
      "registrationTime": "2024-08-20T15:30:00",
      "hoursConfirmed": false
    }
  ]
}
```

### 6. AI鐢熸垚娲诲姩鎻忚堪锛堢鐞嗗憳锛?
**鎺ュ彛**锛歚POST /activity/ai/generate`

**闇€瑕佺鐞嗗憳鏉冮檺**

**璇存槑**锛氬悗绔娇鐢?**DeepSeek**锛圤penAI 鍏煎 `chat/completions`锛夈€傝鍦?**activity-service** 閰嶇疆鐜鍙橀噺 **`DEEPSEEK_API_KEY`**锛屾垨瑙?`application.properties` 涓?`ai.api.*`銆傛湭閰嶇疆鎴栬皟鐢ㄥけ璐ユ椂浠嶈繑鍥?**200**锛屽唴瀹逛负**鏈湴妯℃澘鐢熸垚鐨勫厹搴曟枃妗?*銆?
**璇锋眰绀轰緥**锛?```bash
curl -X POST http://localhost:9000/activity/ai/generate \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "location": "鏍″尰闄㈤棬鍙?,
    "category": "鐖卞績灏忓眿",
    "keywords": "鐚杞? 鐖卞績鏈嶅姟, 鍛ㄥ叚"
  }'
```

**璇锋眰鍙傛暟**锛?```json
{
  "location": "string",   // 娲诲姩鍦扮偣
  "category": "string",   // 娲诲姩绫诲瀷
  "keywords": "string"    // 鍏抽敭璇?}
```

**鍝嶅簲绀轰緥**锛?```json
{
  "code": 200,
  "message": "鐢熸垚鎴愬姛",
  "data": "鐖卞績灏忓眿鐚杞︽椿鍔╘n\n娲诲姩鏃堕棿锛氭湰鍛ㄥ叚涓婂崍9:00-12:00\n娲诲姩鍦扮偣锛氭牎鍖婚櫌闂ㄥ彛\n\n娲诲姩鍐呭锛歕n1. 寮曞鍚屽浠湁搴忕尞琛€\n2. 鎻愪緵鐜板満鍜ㄨ鏈嶅姟\n3. 鍙戞斁鐚绾康鍝乗n4. 缁存姢鐜板満绉╁簭\n\n鎶ュ悕瑕佹眰锛歕n- 韬綋鍋ュ悍锛岃兘澶熺珯绔嬫湇鍔n- 鍏锋湁鑹ソ鐨勬矡閫氳兘鍔沑n- 鏈夌埍蹇冨拰璐ｄ换蹇僜n\n蹇楁効鏃堕暱锛?灏忔椂\n\n璁╂垜浠竴璧蜂紶閫掔埍蹇冿紝娓╂殩浣犳垜锛?
}
```

### 7a. 鑾峰彇鍏ㄩ儴鎶ュ悕鍒楄〃锛堢鐞嗗憳锛?
**鎺ュ彛**锛歚GET /activity/admin/registrations`  
**Query**锛歚activityId`锛堝彲閫夛紝浼犲叆鍒欏彧鏌ヨ娲诲姩涓嬬殑鎶ュ悕锛?
**闇€瑕佺鐞嗗憳鏉冮檺**锛堣姹傚ご `X-User-Role` 鐢辩綉鍏冲湪瑙ｆ瀽 JWT 鍚庢敞鍏ワ級

**璇锋眰绀轰緥**锛?```bash
curl -X GET "http://localhost:9000/activity/admin/registrations" \
  -H "Authorization: Bearer ADMIN_TOKEN"

# 浠呮煡鐪嬫椿鍔?id=1 鐨勬姤鍚?curl -X GET "http://localhost:9000/activity/admin/registrations?activityId=1" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

**鍝嶅簲绀轰緥**锛堝瓧娈典笌 `RegistrationVO` 涓€鑷达紝鍚仈琛ㄧ敤鎴蜂俊鎭級锛?```json
{
  "code": 200,
  "message": "鎴愬姛",
  "data": [
    {
      "id": 1,
      "userId": 2,
      "activityId": 1,
      "activityTitle": "瀛﹂暱鐏偓 - 鏂扮敓鍏ュ寮曞",
      "location": "瀛︽牎涓滈棬杩庢柊鐐?,
      "volunteerHours": 4.0,
      "startTime": "2024-09-01T08:00:00",
      "registrationTime": "2024-08-20T15:30:00",
      "checkInStatus": 0,
      "hoursConfirmed": 0,
      "confirmTime": null,
      "status": "REGISTERED",
      "username": "student01",
      "realName": "寮犱笁",
      "studentNo": "2021101",
      "phone": "13800138001"
    }
  ]
}
```

**璇存槑**锛氫粎杩斿洖 `status=REGISTERED` 鐨勬姤鍚嶏紱鏁版嵁鏉ヨ嚜 `vol_registration` 鑱旇〃 `vol_activity`銆乣sys_user`锛堜笌 `activity-service` 浣跨敤鍚屼竴 MySQL 搴擄級銆?
### 7b. 鑾峰彇鎸囧畾娲诲姩鐨勬姤鍚嶅垪琛紙绠＄悊鍛橈級

**鎺ュ彛**锛歚GET /activity/{activityId}/registrations`

**闇€瑕佺鐞嗗憳鏉冮檺**

**璇锋眰绀轰緥**锛?```bash
curl -X GET http://localhost:9000/activity/1/registrations \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

鍝嶅簲鏍煎紡鍚?**7a** 鐨?`data` 鏁扮粍锛堜粎璇ユ椿鍔級銆?
### 8. 鑾峰彇宸茬粨鏉熷緟鏍搁攢娲诲姩鍒楄〃锛堢鐞嗗憳锛?
**鎺ュ彛**锛歚GET /activity/admin/endedActivities`

**闇€瑕佺鐞嗗憳鏉冮檺**

**璇存槑**锛氳繑鍥?`end_time` 宸茶繃涓?`status != COMPLETED` 鐨勬椿鍔ㄥ垪琛紝鐢ㄤ簬鏃堕暱鏍搁攢妯″潡閫夋嫨娲诲姩銆?
**璇锋眰绀轰緥**锛?```bash
curl http://localhost:9000/activity/admin/endedActivities \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### 9. 鏍囪绛惧埌锛堢鐞嗗憳锛?
**鎺ュ彛**锛歚POST /activity/admin/checkIn/{registrationId}`

**闇€瑕佺鐞嗗憳鏉冮檺**

**璇存槑**锛氬皢鎸囧畾鎶ュ悕璁板綍鐨?`check_in_status` 缃负 `1`锛堝凡绛惧埌锛夈€?
**璇锋眰绀轰緥**锛?```bash
curl -X POST http://localhost:9000/activity/admin/checkIn/3 \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### 10. 鏍搁攢鏃堕暱锛堢鐞嗗憳锛?
**鎺ュ彛**锛歚POST /activity/confirmHours/{registrationId}`

**闇€瑕佺鐞嗗憳鏉冮檺**

**璇锋眰绀轰緥**锛?```bash
curl -X POST http://localhost:9000/activity/confirmHours/1 \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

**鍝嶅簲绀轰緥**锛?```json
{
  "code": 200,
  "message": "鏃堕暱鏍搁攢鎴愬姛",
  "data": null
}
```

**璇存槑**锛?- 鏍搁攢鎴愬姛鍚庯紝`vol_registration.hours_confirmed` 璁句负 `1`锛堝凡鏍搁攢锛?- 鍚屾椂鍐欏叆 `confirm_time`锛堟牳閿€鏃堕棿鎴筹級
- 缁?Feign 璋冪敤 `user-service` 鏇存柊鐢ㄦ埛鐨勭疮璁″織鎰挎椂闀?- 浠?*宸茬粨鏉熶笖鏈粨椤?*鐨勬椿鍔ㄧ殑鎶ュ悕璁板綍鍙牳閿€锛堝墠绔椂闀挎牳閿€妯″潡宸茶繃婊わ級

## 馃И 娴嬭瘯鍦烘櫙

### 鍦烘櫙1锛氬畬鏁寸殑蹇楁効娲诲姩娴佺▼

#### 姝ラ1锛氱鐞嗗憳鐧诲綍
```bash
curl -X POST http://localhost:9000/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

淇濆瓨杩斿洖鐨?`token`銆?
#### 姝ラ2锛氬垱寤烘椿鍔?```bash
curl -X POST http://localhost:9000/activity/create \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "娴嬭瘯娲诲姩",
    "description": "杩欐槸涓€涓祴璇曟椿鍔?,
    "location": "娴嬭瘯鍦扮偣",
    "maxParticipants": 5,
    "volunteerHours": 2.0,
    "startTime": "2026-12-01T10:00:00",
    "endTime": "2026-12-01T12:00:00",
    "registrationStartTime": "2026-11-01T00:00:00",
    "registrationDeadline": "2026-11-30T23:59:59",
    "category": "鍏朵粬"
  }'
```

#### 姝ラ3锛氬織鎰胯€呯櫥褰?```bash
curl -X POST http://localhost:9000/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"student01","password":"password123"}'
```

#### 姝ラ4锛氭煡鐪嬫椿鍔ㄥ垪琛?```bash
curl http://localhost:9000/activity/list
```

#### 姝ラ5锛氭姤鍚嶆椿鍔?```bash
curl -X POST http://localhost:9000/activity/register/1 \
  -H "Authorization: Bearer STUDENT_TOKEN"
```

#### 姝ラ6锛氭煡鐪嬫垜鐨勬姤鍚?```bash
curl http://localhost:9000/activity/myRegistrations \
  -H "Authorization: Bearer STUDENT_TOKEN"
```

#### 姝ラ7锛氱鐞嗗憳鏌ョ湅鎶ュ悕鍒楄〃
```bash
curl http://localhost:9000/activity/admin/registrations \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

浠庤繑鍥炵殑 `data` 涓彇寰呮牳閿€璁板綍鐨?`id`锛岀敤浜庝笅涓€姝ャ€?
#### 姝ラ8锛氱鐞嗗憳鏍搁攢鏃堕暱
```bash
curl -X POST http://localhost:9000/activity/confirmHours/1 \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

锛堝皢 `1` 鏇挎崲涓烘楠?7 涓煇鏉¤褰曠殑 `id`銆傦級

#### 姝ラ9锛氬織鎰胯€呮煡鐪嬫洿鏂板悗鐨勬椂闀?```bash
curl http://localhost:9000/user/info \
  -H "Authorization: Bearer STUDENT_TOKEN"
```

### 鍦烘櫙2锛氶槻瓒呭崠娴嬭瘯

鍒涘缓涓€涓?`maxParticipants=5` 鐨勬椿鍔紝鐒跺悗妯℃嫙 10 涓敤鎴峰悓鏃舵姤鍚嶏細

```bash
# 浣跨敤鑴氭湰鎴?JMeter 骞跺彂璇锋眰
for i in {1..10}; do
  curl -X POST http://localhost:9000/activity/register/1 \
    -H "Authorization: Bearer TOKEN_$i" &
done
wait
```

棰勬湡缁撴灉锛?- 鍓?涓姹傛垚鍔?- 鍚?涓姹傝繑鍥?鍚嶉宸叉弧"

### 鍦烘櫙3锛欽WT杩囨湡娴嬭瘯

1. 鐧诲綍鑾峰彇 Token
2. 绛夊緟 Token 杩囨湡锛堥粯璁?4灏忔椂锛屽彲淇敼閰嶇疆缂╃煭鏃堕棿娴嬭瘯锛?3. 浣跨敤杩囨湡 Token 璇锋眰鎺ュ彛
4. 棰勬湡杩斿洖 `401 Unauthorized`

## 馃攳 璋冭瘯宸ュ叿

### 浣跨敤 Postman

1. 瀵煎叆 API 闆嗗悎
2. 璁剧疆鐜鍙橀噺锛?   - `baseUrl`: `http://localhost:9000`
   - `token`: 鐧诲綍鍚庣殑 Token
3. 鍦?Headers 涓嚜鍔ㄦ坊鍔狅細
   ```
   Authorization: Bearer {{token}}
   ```

### 浣跨敤 cURL

```bash
# 淇濆瓨 Token 鍒板彉閲忥紙Linux/Mac锛?export TOKEN="eyJhbGciOiJIUzI1NiJ9..."

# 浣跨敤鍙橀噺
curl http://localhost:9000/user/info \
  -H "Authorization: Bearer $TOKEN"
```

### 浣跨敤娴忚鍣ㄥ紑鍙戣€呭伐鍏?
1. 鎵撳紑鍓嶇椤甸潰
2. F12 鎵撳紑寮€鍙戣€呭伐鍏?3. Network 鏍囩鏌ョ湅璇锋眰
4. 澶嶅埗涓?cURL 鍛戒护

## 馃摑 甯歌閿欒

### 401 Unauthorized
- **鍘熷洜**锛歍oken 缂哄け銆佹棤鏁堟垨杩囨湡
- **瑙ｅ喅**锛氶噸鏂扮櫥褰曡幏鍙栨柊 Token

### 403 Forbidden
- **鍘熷洜**锛氭潈闄愪笉瓒筹紙濡傛櫘閫氱敤鎴疯闂鐞嗗憳鎺ュ彛锛?- **瑙ｅ喅**锛氫娇鐢ㄥ叿鏈夌浉搴旀潈闄愮殑璐﹀彿

### 503 Service Unavailable
- **鍘熷洜**锛氫笅娓告湇鍔℃湭鍚姩鎴栨湭娉ㄥ唽鍒?Nacos
- **瑙ｅ喅**锛氭鏌ユ湇鍔″惎鍔ㄧ姸鎬佸拰 Nacos 娉ㄥ唽

### 500 Internal Server Error
- **鍘熷洜**锛氭湇鍔″櫒鍐呴儴閿欒
- **瑙ｅ喅**锛氭煡鐪嬪悗绔湇鍔℃棩蹇?
## 馃幆 鎬ц兘娴嬭瘯

### 浣跨敤 Apache Bench

```bash
# 娴嬭瘯鐧诲綍鎺ュ彛 QPS
ab -n 1000 -c 10 -p login.json -T application/json \
  http://localhost:9000/user/login
```

### 浣跨敤 JMeter

1. 鍒涘缓绾跨▼缁?2. 娣诲姞 HTTP 璇锋眰
3. 閰嶇疆骞跺彂鏁板拰寰幆娆℃暟
4. 鏌ョ湅鑱氬悎鎶ュ憡

## 馃搳 鐩戞帶绔偣

### Spring Boot Actuator

璁块棶 http://localhost:9100 鏌ョ湅鐩戞帶涓績锛圫pring Boot Admin锛?
鍙煡鐪嬶細
- 鏈嶅姟鍋ュ悍鐘舵€?- JVM 鍐呭瓨浣跨敤
- HTTP 璇锋眰杩借釜
- 鏃ュ織淇℃伅

---

馃挕 **鎻愮ず**锛氬缓璁娇鐢?Postman 鎴栫被浼煎伐鍏蜂繚瀛樺父鐢ㄧ殑 API 璇锋眰锛屾柟渚垮揩閫熸祴璇曘€?
