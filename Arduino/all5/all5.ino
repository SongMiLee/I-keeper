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

//setup함수는 기계 실행시 최초 1회만 실행되는 함수이다.
//setup함수에서 하는일은 다음과 같다.
//1. 시리얼통신준비(센서간의 통신, 라즈베리파이간의 통신)
//2. 최초 전원이 인가된후 원활한 작동을 위해서 심박센서의 출력값을 저장하는 배열을 채우는 작업을 한다.

void setup() {
	Serial.begin(9600);	// 1번에 해당하는 부분이다.
	while(heart_count < arr_max+10){ //2번에 해당하는 부분이다.
		heart_arr[heart_count++] = analogRead(A2);
	}
	prev_time = millis();//이 변수는 시간간격을 측정할때 쓰이는 변수이다. 
	                     //지금 이 명령어를 통해 현재 시간을 넣었다.
}

void loop() {
	p_s1 = 1024-analogRead(A0); // 각각의 센서로 부터 값을 읽어들이고, 저장한다.
	p_s2 = 1024-analogRead(A1);
	p_s3 = 1024-analogRead(A3);
	p_s4 = 1024-analogRead(A4);
	h_s1 = analogRead(A2);
	
	// 심박센서의 경우 환경에 따라 서로 다른 심장 파형을 그려낸다.
	// 이에 서로다른 파형을 통일된 파형의 모양으로 그려낼 필요가 있다.
	// 아래 3줄의 명령어는 그 작업을 위해 작성된 코드이다.

	insert_to_arr(h_s1);	// 일단 심박센서로 부터 온 값을 배열(heart_arr)에 저장다. 이 함수는 해당 배열을
	                        //  큐와 비슷하게 가장 먼저온 값을 한번 빼고 지금 값을 가장 뒤에 넣는다.
	heart_beat = peak_emphasis(arr_max-2); // 위에 값을 넣은 배열을 이용해서 peak(값이 확 튀는 구간)인지 확인한다
	insert_to_filter_arr(heart_beat);  // 선별결과를 filter_arr라는 배열에 저장한다.

	if(filter_count >= fil_max){//일정량의 filter된 결과가 모이지 전까지는 이 구문을 통과하지 못한다.
		analysis_con = analysis(); // filter_arr에 저장된 파형이 무엇인지 확인하는 함수이다. 
		bool temp = is_running; //이 변수는 is_running의 변경유무를 확인하기 위해 임서 저장되는 변수이다.
		is_running = (error_count>error_thresh)? true: false;  //is_runnung은 현재 안정된 상태인지, 이 안정 상태인지를 저장하는 변수이다.
		if(temp != is_running){ // 만약에 is_running값이 변경되었다면 prev_time은 초기화 해줌으로써 안정되게 작동하도록 한다.
			prev_time = millis();
			if(is_running){ error_count+=20;} 
		}

		if(is_running){ // 현재 상태가 비안정(걷거나 뛰는 상황)인경우
			if(analysis_con >= SHAKE_SPAPE ){//현재 심박 파형이 요동치는 상황(측정불가의 상황)인 경우
				error_count  = (error_count+5 > 70)? 70 : error_count+5;   //err_count를 늘려서 오류상황임을 알린다.
			}
			else{ //단일 peak파형이나 선형파형이 나온 경우
				error_count = (error_count-1 < 0)? 0 : error_count-1;  // 값은 출력하지 않고 err_count를 줄여서 최종적으로
				                                                       // is_running변수가 안정값을 가지도록 해준다.
			}

			if(millis() - prev_time > 300){         // 이 구문은 현재 상태가 비안정 상태인 경우 0.3초마다 출력하게끔 하는 부분이다.
				Serial.print("0 ");
				sort_press_arr();
				send_press_top3(); 
				prev_time = millis();
			}
		}
		else{ // 현재 상태가 안정(가만히 있는) 경우
			if(analysis_con >= SHAKE_SPAPE){//현재 심박 파형이 요동치는 상황(측정불가의 상황)인 경우
				error_count  = (error_count+5 > 70)? 70 : error_count+5;//err_count를 늘려서 오류상황임을 알린다.
			}
			else if(analysis_con == PULSE_SPAPE){ //단일 peak파형만 나온 경우(심박이 감지되었다.) 이경우 값을 라즈베리파이로 전송한다.
				int temp_time = millis()-prev_time; //심박시간을 구한다
				if( temp_time < 400 || temp_time > 1300){ //너무 짧거나 긴 심박의 경우 error로 간주하고 err_count를 증가시킨다.
					error_count  = (error_count+5 > 70)? 70 : error_count+5;
					prev_time = millis();
				}        
				else{// 심박간격이 정상이라면 라즈베리파이로 값을 전동한다.
					error_count = (error_count-1 < 0)? 0 : error_count-1;          
					Serial.print(temp_time); //심박시간을 보내고
					Serial.print(" ");
					sort_press_arr();  //압력센서값의 배열을 정렬한뒤에 압력센서값의 가장 높은 값을 전송한다.
					send_press_top3();        
					prev_time = millis();        
				}
			}
			else{//선형파형이 나온경우
				error_count = (error_count-1 < 0)? 0 : error_count-1;     
			}
		}
		filter_count=0;
	}
	press_arr[0][arr_count] = p_s1; //압력센서값은 계속해서 저장된다.
	press_arr[1][arr_count] = p_s2;
	press_arr[2][arr_count] = p_s3;
	press_arr[3][arr_count] = p_s4;
	arr_count = (arr_count+1>press_arr_max-1)?0:arr_count+1; 
	delay(7); //요게 많이 중요하다. 이게 너무 짧으면 심박은 감지 되지 않으며, 너무 길면 굉장이 이상하게 작동한다.
	          //현재 이 소스는 릴리패드의 사양(cpu = 8Mhz)에 맞게 제작된 소스이므로 다른 성능의 pc에서 사용할 경우 
	          //이값을 바꿔줘야 제대로 작동한다.
}



// peak 선별결과가 저장된 배열(filter_arr)를 이용해서 특정 구간의 파형이 
// 요동치는지, 심박 파형처럼 1번만 peak가 잡혔는지, 수평선인자 검사하는 함수이다.
int analysis(){  
	int count = 0;
	for(int i=0; i<fil_max; i++)
		if(filter_arr[i]>0)
			count++;

	if( count == 0)   return LINE_SPAPE;  
	else if( count < 3) return PULSE_SPAPE; 
	else return count;
}


// peak값인지 판별하는 함수이며, Edge Detecting Thershold을 이용하였다.
// 양 옆의 값이 아닌 왼쪽값만을 빼는 이유는 심장 박동 간격을 구할때 
// 동일한 패턴이 나타날때를 잡아내게 위한 목적과 다양한 에러 상황을 모두 잡아낼 수 있도록 만들었기 때문이다.
int peak_emphasis(int input){ 
	int temp = 0;
	temp = 8*(heart_arr[input]-heart_arr[input-1]);
	if(temp<500) temp =0;		// 이 부분 역시 에러를 잡아내기 위해 threshold를 적용했다.
	                            //  사람 파형에 따라 진동하듯이 오는 경우가 있어서 이 if문이 있으면 걸러낼수 있다.
	if(temp >1023) { temp = 1023; }
	return temp;
}


//heart_arr배열을 queue의 push와 비슷하게 작동하는 함수이다. 배열앞값(arr[0])을 덮어없어지고, 새로운 값이 배열 끝에 들어간다.
void insert_to_arr(int input){ 
	for(int i=0; i<arr_max+10; i++){
		heart_arr[i] = heart_arr[i+1];
	}
	heart_arr[arr_max+9]  = input;
}


//filter_arr배열을 queue의 push와 비슷하게 작동하는 함수이다. 배열앞값(arr[0])을 덮어없어지고, 새로운 값이 배열 끝에 들어간다.
void insert_to_filter_arr(int input){ 
	filter_count++;
	for(int i=0; i<fil_max; i++){
		filter_arr[i] = filter_arr[i+1];
	}
	filter_arr[fil_max-1]  = input;
}


//
void sort_press_arr(){ // 압력센서 값의 배열에서 최대값을 배열 맨 뒤에 두는 함수이다.
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
}
void send_press_top3(){ //각배열의 최대값을 전송하는 함수이다.
	double top_3_temp = arr_count * 1.0;
	int top_3 = top_3_temp;
	for(int i=0; i<4; i++){
		Serial.print(press_arr[i][top_3]);
		Serial.print(" ");
	}
	Serial.println(" ");
	arr_count=0;
}
