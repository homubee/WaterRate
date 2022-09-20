# WaterRate
## Description
- 공용/개인 수도 요금 계산 어플리케이션
- 여러 가구가 별도 계량기 없이 수도를 공유할 때 발생하는 번거로운 요금 계산을 처리해줍니다.
- 공용/개인 수도 정보 저장 기능, 수도 요금 계산 기능, 요금 계산 결과 출력 기능(PDF 형태) 등을 제공합니다.

## Download
> https://play.google.com/store/apps/details?id=com.homubee.waterrate
>
> ※ 안드로이드 버전 및 기종에 따라 일부 기능이 제한될 수 있습니다.

## Stack
<div align=center>
  <img src="https://img.shields.io/badge/kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">
  <img src="https://img.shields.io/badge/sqlite-003B57?style=for-the-badge&logo=sqlite&logoColor=white">
  <img src="https://img.shields.io/badge/android studio-3DDC84?style=for-the-badge&logo=android studio&logoColor=white">
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
</div>

## Environment
- Minimum SDK Version: API 23
- Target SDK Version: API 31
- JVM 1.8

## Functionality
- 공용/개인 수도 저장 기능
  - 이름, 전월지침을 입력 받아 저장
  - 개인 수도 입력 시 각각에서 사용하는 공용 수도 선택 가능
  - 개인 수도의 경우, 별도 계량기 없이 공용 수도만 사용하는 경우도 포함함
- 수도 요금 계산 기능
  - 금월지침, 총사용량, 총요금 입력 받아 요금 계산
  - 공용 수도 요금은 개별로 균등하게 분배되며, 소숫점으로 인한 오차는 적절하게 조정하여 분배
- 요금 계산 결과 출력 기능
  - PDF 형식으로 요금 계산 결과를 출력해 파일로 저장 가능
  - PDF에 이미지 추가 가능
  - 표/글자 크기 조절 가능

## Screenshots
<img src="https://user-images.githubusercontent.com/83688807/155986327-958e755d-9f5a-4a7d-a2b3-01700f3bbb32.png" width="270" height="480"/> <img src="https://user-images.githubusercontent.com/83688807/155986344-bf5358c6-2390-4076-a432-8224ade2deac.png" width="270" height="480"/>

<img src="https://user-images.githubusercontent.com/83688807/155986419-8516c0bd-423f-4478-8c83-8fa4550b3a42.png" width="270" height="480"/> <img src="https://user-images.githubusercontent.com/83688807/155986428-95c56016-0d6e-4048-afda-d907ae96dd0c.png" width="270" height="480"/>

<img src="https://user-images.githubusercontent.com/83688807/155986429-29b18f15-c931-4ffe-8569-580f302dabda.png" width="270" height="480"/> <img src="https://user-images.githubusercontent.com/83688807/155986430-b622e63a-61ff-403a-ade6-e5ecc30cf395.png" width="270" height="480"/>

<img src="https://user-images.githubusercontent.com/83688807/155986433-7f3dd1aa-3a33-4ac5-a688-aa74266cf87e.png" width="270" height="480"/> <img src="https://user-images.githubusercontent.com/83688807/155986435-a4d7e5a2-d161-4a87-93c9-3044a8d46544.png" width="270" height="480"/>

