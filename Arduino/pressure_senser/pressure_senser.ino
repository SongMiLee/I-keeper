int FSRpin1 = A0; // 압력센서를 A0(아날로그 포트번호)로 편하게 부르기 위해 변수값으로 설정.
int FSRpin2 = A1; // 
int FSRpin3 = A2; //
int FSRpin4 = A3; // 
int V1, V2,V3, V4; ; // 센서값을 저장할 변수
float r1,r2,r3,r4;  // 계산값을 저장하기 위한 변수

void setup() {
  Serial.begin(9600); // 센서값을 읽기 위해 시리얼 모니터를 사용할 것을 설정.
}
void loop(){
  V1 = analogRead(FSRpin1); // 아날로그를 입력 받음 (0~1023)
  V2 = analogRead(FSRpin2); // 아날로그를 입력 받음 (0~1023)
  V3 = analogRead(FSRpin3); // 아날로그를 입력 받음 (0~1023)
  V4 = analogRead(FSRpin4); // 아날로그를 입력 받음 (0~1023)
  r1 = ((9.78 * V1)/(1-(V1/1023.0)));
  r2 = ((9.78 * V2)/(1-(V2/1023.0)));
  r3 = ((9.78 * V3)/(1-(V3/1023.0)));
  r4 = ((9.78 * V4)/(1-(V4/1023.0)));
  Serial.print("Rfsr: "); // sensor: 라는 텍스트를 프린트한다.
  Serial.print(r1); 
  Serial.print(", "); 
  Serial.print(r2); 
  Serial.print(", "); 
  Serial.print(r3);  
  Serial.print(", "); 
  Serial.println(r4); 
  delay(500);
}
