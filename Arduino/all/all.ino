/*
라즈베리파이와의 통신 메시지 전송 X.
진동센서 작동 & 계산 X.

*/
int Pulse_pin=A0; //심박센서를 A0(아날로그 포트번호)로 편하게 부르기 위해 변수값으로 설정.
int FSR_pin1=A1;
int FSR_pin2=A2;
int FSR_pin3=A3;
int Var_pin=A4;

int now_time;
int prev_time;

float pul_result = 0; //측정값을 저장하는 변수
float thresh_hold = 0; // 심박 측정시 이 값을 넘으면 심박이 뛴것으로 인정
int divide_level = 6;  // level을 나누는 구간의 수
int one_term_length;  //level을 나누는 구간의 길이

float RRI_prev_value = 0; 
int FSR_max_value1 = 0;
int FSR_max_value2 = 0;
int FSR_max_value3 = 0;
int Var_max_value = 0;

float Pulse_r = 0;  //계산값을 저장하기 위한 변수
int FSR_r1=0;
int FSR_r2=0;
int FSR_r3 =0;
int Var_r=0;

int RRI[10]={0};
int RRI_avg, RRI_sum =0;
int count = 0;

int temp;

void setup() {
  Serial.begin(9600);
  Serial.println("Waiting For Start.......");
  delay(1000);
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
    thresh_hold = thresh_hold-3;
  }  
  one_term_length  = thresh_hold / divide_level;
  thresh_hold = thresh_hold/(double)one_term_length;
  thresh_hold = thresh_hold * thresh_hold * thresh_hold;
  thresh_hold = thresh_hold * 0.77;
  Serial.println(thresh_hold);
}

void loop() {
  RRI_prev_value = Pulse_r;
  
  pul_result = analogRead(Pulse_pin)/one_term_length; //값의 미세한 차이를 증폭시키기 위한 계산들...
  FSR_r1 = 1024 - analogRead(FSR_pin1);
  FSR_r2 = 1024 - analogRead(FSR_pin2);
  FSR_r3 = 1024 - analogRead(FSR_pin3);
  Var_r = analogRead(Var_pin);  
  Pulse_r=pul_result*pul_result*pul_result; //  
  delay(30);
  
  if(FSR_r1 > FSR_max_value1){ FSR_max_value1 = FSR_r1;}
  if(FSR_r2 > FSR_max_value2){FSR_max_value2 = FSR_r2;}
  if(FSR_r3 > FSR_max_value3){FSR_max_value3 = FSR_r3;}
  if(Var_r > Var_max_value){Var_max_value = Var_r;}

//  Serial.print(now_time);
  //Serial.print(", ");
  Serial.print(FSR_r1);
  Serial.print(", ");
  Serial.print(FSR_r2);
  Serial.print(", ");
  Serial.print(FSR_r3);
  Serial.print(", ");
  Serial.println(Var_r);
  
  if(Pulse_r>thresh_hold){
    now_time = millis() - prev_time;
    Serial.println(now_time);
    if(count >= 10){
      RRI_avg, RRI_sum =0;
      for(int i=0; i<count; i++){ 
        RRI_sum += RRI[i];
      }    
      RRI_avg=RRI_sum/count;      
      Serial.print(RRI_sum);
      Serial.print(", ");
      Serial.print(RRI_avg);
      Serial.print(", ");
      Serial.print(FSR_max_value1);
      Serial.print(", ");
      Serial.print(FSR_max_value2);
      Serial.print(", ");
      Serial.print(FSR_max_value3);
      Serial.print(", ");
      Serial.println(Var_max_value);
      count=0; 
      FSR_max_value1  = FSR_max_value2 = FSR_max_value3 = Var_max_value = 0;
    }
    else{
      RRI[count++] = now_time;
    }
    prev_time = millis();
    delay(380);    
  }
}
