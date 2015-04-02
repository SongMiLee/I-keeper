int Pulse_pin=A0; //심박센서를 A0(아날로그 포트번호)로 편하게 부르기 위해 변수값으로 설정.
int now_time;
int prev_time;
float pul_result = 0; //측정값을 저장하는 변수
float thresh_hold = 0;
float prev_value;
float r = 0;  //계산값을 저장하기 위한 변수

void setup() {
  Serial.begin(9600);
  Serial.println("Waiting For Start.......");
  prev_time = millis();  
  
  while(prev_time<10000){
    pul_result=analogRead(Pulse_pin);  
    if(thresh_hold < pul_result){
      thresh_hold = pul_result;
    }
    prev_time = millis();
    Serial.print("prev:");
    Serial.print(prev_time);
    Serial.print(",");
    Serial.print("thresh:");
    Serial.println(thresh_hold);
  }  
  thresh_hold = thresh_hold / 190;
  thresh_hold = thresh_hold * thresh_hold * thresh_hold;
  thresh_hold = thresh_hold * 0.77;
  Serial.println(thresh_hold);
}

void loop() {
  prev_value = r;
  pul_result=analogRead(Pulse_pin)/190; //값의 미세한 차이를 증폭시키기 위한 계산들...
  r=pul_result*pul_result*pul_result; //
  delay(10);
  /*if(prev_value > r){
    now_time = prev_time - millis();
    Serial.println(now_time);
    prev_time = millis();
    delay(380);    
  }*/
  
  if(r>thresh_hold){
    now_time = prev_time - millis();
    Serial.println(now_time);
    prev_time = millis();
    delay(380);    
  }
  /*Serial.print(prev_value);
  Serial.print(" ");
  Serial.println(r);
  */
}
