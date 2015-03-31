int Pulse_pin=A0; //심박센서를 A0(아날로그 포트번호)로 편하게 부르기 위해 변수값으로 설정.
float pul_result; //측정값을 저장하는 변수
float r;  //계산값을 저장하기 위한 변수

void setup() {
  Serial.begin(9600);
}

void loop() {
  pul_result=analogRead(Pulse_pin)/250; //값의 미세한 차이를 증폭시키기 위한 계산들...
  r=pul_result*pul_result*pul_result; //

  Serial.println(r);
  //Serial.println(pul_result);
  delay(20);
}
