
int press_arr[4][100];
int arr_count = 0;

//운동오류 관련된 변수들
bool is_running;
int error_thresh =8;
int error_count = 0;

//센서 입력관련 변수들
int p_s1, p_s2, p_s3, p_s4;
int h_s1 = 0;
int heart_arr[6];
int heart_count = 0;
int beat_thresh =250;

//시간관련 함수들
int prev_time;
int time_gap;

void setup(){
	Serial.begin(9600);
	prev_time = millis();
        Serial.println("start");
}

void loop(){        
	//센서값을 읽는다
	p_s1 = 1024-analogRead(A0);
	p_s2 = 1024-analogRead(A1);
	p_s3 = 1024-analogRead(A3);
	p_s4 = 1024-analogRead(A4);
	h_s1 = analogRead(A2);

	//상황판단을 위한 알맞는 계산을 한다.
	if(error_thresh < error_count){ is_running = true; }
	else{  is_running = false; }
	time_gap = millis() - prev_time;
        /*Serial.print(max_v(prev_heart1,prev_heart2,h_s1));
        Serial.print(", ");
        Serial.println(min_v(prev_heart1,prev_heart2,h_s1));*/
        heart_gap = max_v(prev_heart1,prev_heart2,h_s1) - min_v(prev_heart1,prev_heart2,h_s1);
        
	if(is_running){//전체 흐름이 운동중이라고 생각하는 경우   
		if( heart_gap > beat_thresh || heart_gap < (-1*beat_thresh) ){//지금을 운동중이라고 판단되는 경우
			error_count++;
			if(error_count>30)
				error_count = 30;			
		}

		else //지금을 안정된 상황이라고 판단되는 경우
			error_count--;		
				
		if(time_gap>1000){
  /*
			Serial.print();
			Serial.print();
			Serial.print();
			Serial.print();*/
			Serial.println("runnung - time1000");
			prev_time = millis();
		}
	}

	else{//전체 흐름이 안정된 상태라고 생각판단.                
		if( heart_gap > beat_thresh || heart_gap < (-1 * beat_thresh) ){//지금을 운동중이라고 생각되는 경우
			error_count++;
			if(error_count>30){
				error_count = 30;
			}

			//운동방식에 맞는 행동방식 ㄱㄱㅆ.
		}
		 //지금을 안정된 상태이면서 오류가 아닌경우
		if( heart_gap > beat_thresh){ // 심박이 뛴경우( = 압력센서값들 정렬이 필요 + 센서값중 상위 3퍼의 값 선택)
			for(int i=0; i<4; i++){
				for(int j=0; j<arr_count; j++){
					for(int k=1; k<arr_count; k++){
						int temp;
						if(press_arr[i][j]>press_arr[i][j]){
							temp = press_arr[i][j];
							press_arr[i][j] = press_arr[i][k];
							press_arr[i][k] = temp;
						}	
					}
				}
			}
			double top_3_temp = arr_count*.097;
			int top_3 = top_3_temp;
			/*      
                        Serial.print();
			Serial.print();
			Serial.print();
			Serial.print();*/
			Serial.print(time_gap);
                        Serial.print(", ");
                        Serial.print(heart_gap);
			Serial.println(", heart_beat!!!");
			prev_time = millis();
                        arr_count=0;
                        prev_heart2 = prev_heart1 = h_s1;
                        delay(500);
		}

		else{ //심박이 안뛴경우( = 압력센서값을 저장해놓은 배열에 갑 넣기.)
			press_arr[0][arr_count] = p_s1;
			press_arr[1][arr_count] = p_s2;
			press_arr[2][arr_count] = p_s3;
			press_arr[3][arr_count++] = p_s4;
			error_count--;
                        /*Serial.print(p_s1);
                        Serial.print(", ");
                        Serial.print(p_s2);
                        Serial.print(", ");
                        Serial.print(p_s3);
                        Serial.print(", ");
                        Serial.print(p_s4);
                        Serial.print(", ");
                        Serial.print(heart_gap);
                        Serial.println(", insert complete");*/
                        if(arr_count>100) arr_count = 0;
		}
		
	}
	delay(3);
}

int max_v(int a, int b, int c){
  if(a>b){
    if(a>c){ return a; }    
    else{ return c; }
  }
  else{
    if(b>c){return b;}
    else{return c;}
  }
}

int min_v(int a, int b, int c){
  if(a>b){
    if(b>c){ return c; }    
    else{ return b; }
  }
  else{
    if(a>c){return c;}
    else{return a;}
  }
}
