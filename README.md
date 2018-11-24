# Spring Security JSON login example

## このサンプルの目的
Spring Securityのログインの部分を、JSONリクエストに差し替えたもの。

以下の挙動を実現する。

* JSONリクエストによる、メールアドレス / パスワードでログイン
* ログイン後に、リダイレクトしない
* 未ログイン時に、要認証APIへのアクセスは401を返す
* ログインしていても、権限のないAPIへのアクセスは403を返す
* レスポンスボディの内容をカット
* ログインしても、ログアウトしてもリダイレクトしない

設定自体の実装詳細は、`com.github.charon.r13b.spring.config.SecurityConfig`を参照。

## 構成

* Spring Boot / Spring Web MVC / Spring Security
* Doma 2 / Spring Boot Doma Starter
* H2 Database

## 仕様
ユーザーは、以下のものを用意。

| ユーザーID（メールアドレス） | ロール |
|:-|:-:|
|user01@example.com| `ROLE_USER` |
|admin01@example.com| `ROLE_ADMIN` |

APIは、以下のものを用意

| URL | レスポンス内容 | 未認証アクセス | `USER`アクセス | `ADMIN`アクセス |
|:-|:-:|
| `/public/` | メッセージ | ○ | ○ | ○ |
| `/secure/user` | メッセージ＋ログインユーザー名 | × | ○ | ○ |
| `/secure/admin` | メッセージ＋ログインユーザー名 | × | × | ○ |
| `/secure/me` | ログイン中のユーザーの情報 | × | ○ | ○ |
| `/login` | （なし） | ○ | ○ | ○ |
| `/logout` | （なし） | ○ | ○ | ○ |

## 実行結果

### 実行
```shell
$ mvn spring-boot:run
```

IDE上から、`com.github.charon.r13b.spring.App`を実行しても良し。

### 未ログイン
アクセス可能。
```shell
$ curl -i localhost:8080/public
HTTP/1.1 200
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/plain;charset=UTF-8
Content-Length: 13
Date: Sat, 24 Nov 2018 11:51:21 GMT

Hello World!!
```

ログインしていないので、401。
```shell
$ curl -i localhost:8080/secure/user
HTTP/1.1 401
Set-Cookie: JSESSIONID=97B7ADD922C3D7D4D8B32B114AD892C8; Path=/; HttpOnly
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Length: 0
Date: Sat, 24 Nov 2018 11:51:48 GMT


$ curl -i localhost:8080/secure/admin
HTTP/1.1 401
Set-Cookie: JSESSIONID=4220B29298119D911E57140DE19A6A3A; Path=/; HttpOnly
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Length: 0
Date: Sat, 24 Nov 2018 11:51:51 GMT


$ curl -i localhost:8080/secure/me
HTTP/1.1 401
Set-Cookie: JSESSIONID=631480F96E980B66441B3BD83A3E7CAB; Path=/; HttpOnly
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Length: 0
Date: Sat, 24 Nov 2018 11:51:54 GMT
```

### `USER`ROLEのユーザー

ログイン（`cookie.txt`にCookieの内容を保存）。
```shell
$ curl -b cookie.txt -c cookie.txt -i -XPOST -H 'Content-Type: application/json' localhost:8080/login -d '{"email": "user01@example.com", "password": "password"}'
HTTP/1.1 200
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Set-Cookie: JSESSIONID=493580103ACEA00038766D530AF2CD95; Path=/; HttpOnly
Content-Length: 0
Date: Sat, 24 Nov 2018 11:54:10 GMT
```

アクセス可能な範囲。
```shell
$ curl -b cookie.txt -c cookie.txt -i localhost:8080/public
HTTP/1.1 200
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/plain;charset=UTF-8
Content-Length: 13
Date: Sat, 24 Nov 2018 11:54:50 GMT

Hello World!!


$ curl -b cookie.txt -c cookie.txt -i localhost:8080/secure/user
HTTP/1.1 200
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/plain;charset=UTF-8
Content-Length: 30
Date: Sat, 24 Nov 2018 11:54:56 GMT

Normal User [磯野 カツオ]


$ curl -b cookie.txt -c cookie.txt -i localhost:8080/secure/me
HTTP/1.1 200
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Sat, 24 Nov 2018 11:54:59 GMT

{"email":"user01@example.com","name":"磯野 カツオ","role":"ROLE_USER"}
```

アクセス不可。
```shell
$ curl -b cookie.txt -c cookie.txt -i localhost:8080/secure/admin
HTTP/1.1 403
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Length: 0
Date: Sat, 24 Nov 2018 11:55:48 GMT
```

ログアウト。
```shell
$ curl -b cookie.txt -c cookie.txt -i localhost:8080/logout
HTTP/1.1 200
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Length: 0
Date: Sat, 24 Nov 2018 11:56:10 GMT
```

### `ADMIN`ROLEのユーザー
ログイン。
```shell
$ curl -b cookie.txt -c cookie.txt -i -XPOST -H 'Content-Type: application/json' localhost:8080/login -d '{"email": "admin01@example.com", "password": "password"}'
HTTP/1.1 200
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Set-Cookie: JSESSIONID=0C0662683A111202AA73702B22A81323; Path=/; HttpOnly
Content-Length: 0
Date: Sat, 24 Nov 2018 12:01:21 GMT
```

アクセス可能な範囲。
```shell
$ curl -b cookie.txt -c cookie.txt -i localhost:8080/public
HTTP/1.1 200
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/plain;charset=UTF-8
Content-Length: 13
Date: Sat, 24 Nov 2018 12:01:43 GMT

Hello World!!


$ curl -b cookie.txt -c cookie.txt -i localhost:8080/secure/user
HTTP/1.1 200
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/plain;charset=UTF-8
Content-Length: 27
Date: Sat, 24 Nov 2018 12:01:47 GMT


$ curl -b cookie.txt -c cookie.txt -i localhost:8080/secure/admin
HTTP/1.1 200
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/plain;charset=UTF-8
Content-Length: 26
Date: Sat, 24 Nov 2018 12:01:51 GMT


$ curl -b cookie.txt -c cookie.txt -i localhost:8080/secure/me
HTTP/1.1 200
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Sat, 24 Nov 2018 12:01:53 GMT

{"email":"admin01@example.com","name":"磯野 波平","role":"ROLE_ADMIN"}
```

ログアウト。
```shell
$ curl -b cookie.txt -c cookie.txt -i localhost:8080/logout
HTTP/1.1 200
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Length: 0
Date: Sat, 24 Nov 2018 12:02:43 GMT
```

### オマケ
ログイン失敗の場合。
```shell
$ curl -b cookie.txt -c cookie.txt -i -XPOST -H 'Content-Type: application/json' localhost:8080/login -d '{"email": "baduser@example.com", "password": "password"}'
HTTP/1.1 401
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Length: 0
Date: Sat, 24 Nov 2018 12:03:32 GMT
```
