int Pulse_pin=A0; //심박센서를 A0(아날로그 포트번호)로 편하게 부르기 위해 변수값으로 설정.

int now_time;
int prev_time;

float pul_result = 0; //측정값을 저장하는 변수
float thresh_hold = 0; // 심박 측정시 이 값을 넘으면 심박이 뛴것으로 인정
int divide_level = 6;  // level을 나누는 구간의 수
int one_term_length;  //level을 나누는 구간의 길이

float prev_value; 
float r = 0;  //계산값을 저장하기 위한 변수

int RRI[10]={0};
int count = 0;

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
    thresh_hold = thresh_hold-5;
  }  
  one_term_length  = thresh_hold / divide_level;
  thresh_hold = thresh_hold/(double)one_term_length;
  thresh_hold = thresh_hold * thresh_hold * thresh_hold;
  thresh_hold = thresh_hold * 0.77;
  Serial.println(thresh_hold);
}

void loop() {
  prev_value = r;
  pul_result=analogRead(Pulse_pin)/one_term_length; //값의 미세한 차이를 증폭시키기 위한 계산들...
  r=pul_result*pul_result*pul_result; //
  delay(20);
  /*if(prev_value > r){
    now_time = prev_time - millis();
    Serial.println(now_time);
    prev_time = millis();
    delay(380);    
  }*/
  
  if(r>thresh_hold){
    now_time = prev_time - millis();
    Serial.println(now_time);
    if(count >= 10){
      int RRI_avg, RRI_sum =0;
      for(int i=0; i<count; i++){
        RRI_sum += RRI[i];
        Serial.print(RRI_sum);
        Serial.print(", ");
        Serial.println(RRI[count]);
      }    
      RRI_avg=RRI_sum/count;
      count=0;
      Serial.print(RRI_sum);
      Serial.print(", ");
      Serial.println(RRI_avg);
    }
    else{
      RRI[count++] = now_time;
    }
    prev_time = millis();
    delay(380);    
  }
  /*Serial.print(prev_value);
  Serial.print(" ");
  Serial.println(r);
  */
}
