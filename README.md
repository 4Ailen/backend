# ⛵ Friend Ship
[협업 기록](https://hulking-edge-c2d.notion.site/4Alien-s-Team-94abf384bcd84982bdeaebebff6fabf7?pvs=4)
<!-- 로고 -->
<!-- <p align="center">
  <br>
  <img src="./images/common/logo-sample.jpeg">
  <br>
</p>
 -->
 
# 👋 프로젝트 소개
> 부경대학교 외국인 재학생을 위한 커뮤니티 및 다문화 교류 네트워크 활성화를 위한 애플리케이션
- 교내에서 이루어지고 있는 행사 중 인기가 많은 오프라인 매칭 프로그램을 온라인으로 구현하여 접근성을 향상
- 네트워킹을 위한 플랫폼으로 애플리케이션을 통해 구현
- 어플 이름인 Friend Ship은 우정을 의미하는 "Friend"와 부경대, 부산의 이미지를 상징하는 "ship"을 합함.

# 😀 도입 배경
> 현재 외국인 재학생들이 친구를 사귀는 방식에서 문제점 발견


1. 같은 수업을 듣는 학우와 친해지기 힘들다.

   - 코로나를 통해 서로 간에 다가가기 힘든 주변 분위기를 극복하기 어려움, 실제 한국인 재학생들도 같은 어려움을 겪고 있으나 외국인 재학생들의 경우, 코로나가 더욱 큰 장애요소로 작용하여 같은 수업만으로 친구를 사귀기에는 부족함이 많음.
2. 친구의 친구를 사귀기
   - 1번 방식에서부터 어려움을 가지고 있는 학생은 2번 방법에 대해 접근부터 불가능함.
3. 학교 내부 프로그램을 통해 친해지기
   - 학교 오프라인 프로그램 참석을 위한 회비 및 제한된 인원 수가 존재함.
   - 외향적인 사람의 경우 접근할 수 있지만 내향적인 사람의 경우 해당 프로그램을 사용하기에 접근성이 낮을 수 있음.

# ✔️ 기존 시장 위협자인 Tinder, Wemeet와의 차별점
> 기존 매칭 관련 애플리케이션의 경우, 이성과의 만남이 주 목적이며 특정 단체에 속한 인원들을 찾기 어렵다. 
> 그러나 Friend Ship 애플리케이션의 경우 부경대 외국인 재학생들만이 사용할 수 있도록 홍보 시 특수 코드를 발급하여 회원가입 절차를 진행할 예정이며,
> 이성과의 만남보다는 친구 사귀기에 주목할 수 있도록 성별에 국한 받지 않는 매칭 방식을 사용한다.

| 항목 | Tinder |  Wemeet   |  Friend Ship   |
| :--------: | :--------: | :------: | :-----: |
|   접근성    |   높음    | 높음 | 높음 |
|   사용자    |   anyone    | anyone | OnlyPKNU학생 |
|   성별에 국한    |   O    | O | X |

# ⌛ 매칭 전략
> 일주일 중 토요일까지 매칭 접수를 받고, 일요일 단 하루 매칭을 시작
- 접수 시 받을 정보 두 가지(선호 언어, 질문 답)를 통해 두 개의 질문지가 같은 인원들끼리 매칭 시켜준다.
- 일주일 중 6일의 기다림을 통해 기회라는 인식은 갖게 하여 하루 동안 매칭되는 인원을 조심스럽게 대할 수 있게끔 분위기 조성
   - 현 매칭 전략과 달리 매 순간 새로운 사람과 매칭될 경우, 가벼운 만남으로 이어질 수 있기 때문에 6일의 접수와 단 하루의 매칭 대화가 필요하다 판단.

# 📃 ERD
> 다음 아래와 같은 ERD 작성으로 채팅과 매칭에 대한 서비스를 구현

<img width="1013" alt="ALIENS_ERD" src="https://user-images.githubusercontent.com/77786996/215273739-68c1946e-3913-403a-9f82-dbb1b7a5e0d8.png">

# 🧑‍💻 기술 스택
| 언어 | Dart | Java |  |
| :--------: | :--------: | :------: | :------: |
| 프론트엔드 | Flutter(Dart) |     |  |
| 백엔드 | Spring(Java) |  |  |
| 데이터베이스 | Redis | MYSQL |  |
| 배포 | Doker | Server(AWS) | Kubernetes |

# 🙏 결과 및 기대효과
> 결과물
- 학교에서 가장 인기 많은 매칭 프로그램을 온라인 플랫폼인 애플리케이션으로 구현

> 기대효과
- 평소 부경대학교 내에서 학생들과 교류가 어려웠던 외국인 학생들에게 만남 기회 제공

- 국제 교류 본부에 연락하여 외국인 재학생들에게 홍보
- 교내 홍보물 부착, 플레이스토어, 앱스토어 배포
- 플러터를 활용한 크로스 플랫폼 앱 개발로 안드로이드와 아이폰 사용자 모두 사용 가능, 높은 접근성으로 사용자 유입 및 유치 가능
- 홍보 및 서버비 유지를 위해 링크사업단과의 연계, 학생 계발 역량과 왕의 연계, 외국인 카페 홍보, 카페의 드나 학교에서 진행하는 외국인 재학생 행사시 홍보
- 해당 어플은 현재 부경대학교를 대상으로 만들어졌으나 활성화 시 다른 대학에서도 사용 가능(활성화 시 타 대학 외국인 학생들이 부경대에 대한 긍정적인 인식을 가질 수 있음) -결과를 바탕으로 다 대학교에 홍보 및 협력 제안
  - 교외 학생들 간 교류 가능하도록 확장
  - 부산외대, 동의대, 부산대 등 확장
