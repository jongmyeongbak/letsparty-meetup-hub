package com.letsparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration;

// exclude - 부트 서버 동작을 지연시키는 EC2 인스턴스 메타데이터 서비스를 끔.
//	- 해당 서비스는 EC2 환경에서만 필요함.
//	- 켜둬도 로컬 환경 실행은 가능하지만, 엔드포인트에 접근할 수 없어 SdkClientException을 발생시킴.
@SpringBootApplication(exclude = ContextInstanceDataAutoConfiguration.class)
public class LetspartyMeetupHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(LetspartyMeetupHubApplication.class, args);
	}

}
