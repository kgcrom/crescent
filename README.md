# Crescent (Search Engine)

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/951bfb4baed5405097345352188d6dac)](https://www.codacy.com/gh/kgcrom/crescent/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=kgcrom/crescent&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/951bfb4baed5405097345352188d6dac)](https://www.codacy.com/gh/kgcrom/crescent/dashboard?utm_source=github.com&utm_medium=referral&utm_content=kgcrom/crescent&utm_campaign=Badge_Coverage)

SpringBoot와 Lucene을 이용한 간단한 검색서버입니다.

학습하고 이해한 글과 코드를 적용하고 고민하기 위해 시작했습니다.

## How to Start
```shell
$> ./gradlew build
$> java -jar -Dspring.profiles.active=local crescent_core_web/target/libs/crescent_core_web-0.5-SNAPSHOT.jar
$> # http://localhost:8080/admin  <-- admin page
```
