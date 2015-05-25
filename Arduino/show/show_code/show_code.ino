/*
이 소스는 릴리패드의 출력값을 검사하기 위해 제작된소스입니다.
Processing과 연동해서 사용하면 그래프를 통해 시각적인 변화를 쉽게 파악할 수 있습니다.
*/

#define arr_max 20

int p_s1, p_s2, p_s3, p_s4;
int h_s1 = 0;
int heart_arr[arr_max] = {0};
int heart_count = 0;
int heart_beat;
int temp_timer;
void setup() {
	Serial.begin(9600);
	while(heart_count < arr_max){          
		heart_arr[heart_count++] = analogRead(A2);
	}
}

void loop() {
	p_s1 = 1024-analogRead(A0);
	p_s2 = 1024-analogRead(A1);
	p_s3 = 1024-analogRead(A3);
	p_s4 = 1024-analogRead(A4);
	h_s1 = analogRead(A2);

	insert_to_arr(h_s1);
	heart_beat = peak_emphasis(arr_max-2);
	Serial.print(h_s1);  Serial.print(" ");
	Serial.print(heart_beat);  Serial.print(" ");
	Serial.print(p_s1);  Serial.print(" ");
	Serial.print(p_s2);  Serial.print(" ");
	Serial.print(p_s3);  Serial.print(" ");
	Serial.print(p_s4);  Serial.println(" ");

}

int peak_emphasis(int input){//peak검출 알고리즘( all5 소스에 있는 것 그대로 채용. )
	int temp = 0;
	temp = 8*(heart_arr[input]-heart_arr[input-1]);        

	if(temp<500) temp =0;
	if(temp >1023) { temp = 1023; }
	return temp;
}

void insert_to_arr(int input){ //all5 소스에 있는 것 그대로 채용
	for(int i=0; i<arr_max; i++){
		heart_arr[i] = heart_arr[i+1];
	}
	heart_arr[arr_max]  = input;
}

