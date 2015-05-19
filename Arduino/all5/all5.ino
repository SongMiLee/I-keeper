#define arr_max 20
#define fil_max 4
#define SHAKE_SPAPE 4
#define PULSE_SPAPE 1
#define LINE_SPAPE 0
#define press_arr_max 150

int press_arr[4][press_arr_max];
int arr_count = 0;

//운동오류 관련된 변수들
bool is_running = false;
int heart_beat;
int error_thresh =17;
int error_count = 0;

//센서 입력관련 변수들
int p_s1, p_s2, p_s3, p_s4;
int h_s1 = 0;
int heart_arr[arr_max+10] = {0};
int heart_count = 0;
int filter_arr[fil_max];
int filter_count =0 ;

int analysis_con;

//시간관련 함수들
unsigned long int prev_time;
int time_gap;

void setup() { 
  Serial.begin(9600);	
  while(heart_count < arr_max+10){          
    heart_arr[heart_count++] = analogRead(A2);
  }
  prev_time = millis();
}

void loop() {
  p_s1 = 1024-analogRead(A0);
  p_s2 = 1024-analogRead(A1);
  p_s3 = 1024-analogRead(A3);
  p_s4 = 1024-analogRead(A4);
  h_s1 = analogRead(A2);
  
  insert_to_arr(h_s1);
  heart_beat = peak_emphasis(arr_max-2);
  insert_to_filter_arr(heart_beat);  
  /*Serial.println(heart_beat);*/
 
  if(filter_count >= fil_max){
    //Serial.println(error_count);
    analysis_con = analysis();
    bool temp = is_running;
    is_running = (error_count>error_thresh)? true: false;
    if(temp != is_running){
     prev_time = millis();
    if(is_running){ error_count+=20;} 
   }
    
    if(is_running){
      //Serial.println("i'm running");
      if(analysis_con >= SHAKE_SPAPE ){
        //Serial.println("i'm running-shake");
        error_count  = (error_count+5 > 70)? 70 : error_count+5;        
      }
      else{
        //Serial.println("i'm running-line");
        error_count = (error_count-1 < 0)? 0 : error_count-1;        
        }
        
//        Serial.print(millis());
//         Serial.print(" ");
//         Serial.println(prev_time);
       if(millis() - prev_time > 300){         
          Serial.print("0 ");
          sort_press_arr();
          send_press_top3(); 
          prev_time = millis();
      }
    }
    else{
      //Serial.println("i'm standing");
      //Serial.println(analysis_con);
      if(analysis_con >= SHAKE_SPAPE){
        //Serial.println("i'm standing - shake");
        error_count  = (error_count+5 > 70)? 70 : error_count+5;
      }
      else if(analysis_con == PULSE_SPAPE){
        int temp_time = millis()-prev_time;
        if( temp_time < 400 || temp_time > 1300){
          //Serial.println("i'm standing-one. but too shoert");
          error_count  = (error_count+5 > 70)? 70 : error_count+5;
          prev_time = millis();
        }        
        else{
          //Serial.println("==============================i'm standing-one");
          error_count = (error_count-1 < 0)? 0 : error_count-1;          
          Serial.print(temp_time);
          Serial.print(" ");
          sort_press_arr();
          send_press_top3();        
          prev_time = millis();        
        }
      }
      else{
        //Serial.println("i'm standing-line");
        error_count = (error_count-1 < 0)? 0 : error_count-1;     
      }
    }
    filter_count=0;
  }
  press_arr[0][arr_count] = p_s1;
  press_arr[1][arr_count] = p_s2;
  press_arr[2][arr_count] = p_s3;
  press_arr[3][arr_count] = p_s4;
  arr_count = (arr_count+1>press_arr_max-1)?0:arr_count+1; 
 delay(7);
}

int analysis(){
  int count = 0;
  for(int i=0; i<fil_max; i++)
    if(filter_arr[i]>0)
      count++;
     
  if( count == 0)   return LINE_SPAPE;  
  else if( count < 3) return PULSE_SPAPE; 
  else return count;
}

int peak_emphasis(int input){
  int temp = 0;
  temp = 8*(heart_arr[input]-heart_arr[input-1]);
  if(temp<500) temp =0;
  if(temp >1023) { temp = 1023; }
  return temp;
}

void insert_to_arr(int input){
  for(int i=0; i<arr_max+10; i++){
    heart_arr[i] = heart_arr[i+1];
  }
  heart_arr[arr_max+9]  = input;
}

void insert_to_filter_arr(int input){
//   for(int i=0; i<fil_max; i++){
//    Serial.print(filter_arr[i]);
//    Serial.print(" ");
//  }
//   Serial.println(" ");

//Serial.print(filter_arr[0]);Serial.print(" ");
//Serial.print(filter_arr[1]);Serial.print(" ");
//Serial.print(filter_arr[2]);Serial.print(" ");
//Serial.print(filter_arr[3]);Serial.print(" ");
//Serial.print(filter_arr[4]);Serial.print(" ");
//Serial.print(filter_arr[5]);Serial.println(" ");


  filter_count++;
  for(int i=0; i<fil_max; i++){
    filter_arr[i] = filter_arr[i+1];
  }
  filter_arr[fil_max-1]  = input;
}

void sort_press_arr(){
  int top, temp; 
  for(int i=0; i<4; i++){
    top = press_arr[i][0];
    for(int j=1; j<arr_count; j++){
      if(top < press_arr[i][j]){
        top = press_arr[i][j];
      }
    }
    press_arr[i][arr_count]=top;
  }
  
//  for(int i=0; i<4; i++){
//    for(int j=0; j<arr_count; j++){
//      for(int k=1; k<arr_count; k++){
//        int temp;
//        if(press_arr[i][j]>press_arr[i][j]){
//          temp = press_arr[i][j];
//          press_arr[i][j] = press_arr[i][k];
//          press_arr[i][k] = temp;
//        }	
//      }
//    }
//  }
  
  
}
void send_press_top3(){
  double top_3_temp = arr_count * 1.0;
  int top_3 = top_3_temp;
  for(int i=0; i<4; i++){
    Serial.print(press_arr[i][top_3]);
    Serial.print(" ");
  }
  Serial.println(" ");
  arr_count=0;
}
