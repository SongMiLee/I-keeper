// accelation value
int acc_x=0;
int acc_y=0;
int acc_z=0;
int pre_x, pre_y, pre_z;
int dis_x, dis_y, dis_z;

//pressure value
int presure;

//heart value
int prev_heart;
int now_heart=512;
int is_run_count = 10;
int err_count=0;
int temp_heart;

//sensors
int acc_x_sen = A0;
int acc_y_sen = A1;
int acc_z_sen = A2;
int heart_sen = A3;
int press_sen = A4;

// times
int now_time = 300;
int prev_time;;
int is_correct_count = 10;
int reset_time = 12;

void setup() {
	Serial.begin(9600);
        prev_time = millis();
}

void loop() {
	prev_heart = now_heart;
	pre_x = acc_x;
	pre_y = acc_y;
	pre_z = acc_z;

	now_heart = analogRead(heart_sen);
	acc_x = analogRead(acc_x_sen);
	acc_y = analogRead(acc_y_sen);
	acc_z = analogRead(acc_z_sen);


	temp_heart = now_heart-prev_heart;
	if((temp_heart > 300 || temp_heart < -300 || now_time < 100) && is_correct_count<4){
                Serial.print("error!!  " );
                Serial.print(temp_heart);
		Serial.print(", ");
		Serial.println(now_time);                
		err_count ++;
		prev_heart = now_heart;
		if(is_run_count < err_count){
                        err_count = 0;
                        now_time = 300;
                        Serial.println("is running?");
			delay(3000);
		}
	}
	else{
                if(now_heart > 850){
                        now_time = millis() - prev_time;      
                        if( now_time < 200){
                          is_correct_count-=2;                         
                        }  
                        else{
                          Serial.print("------------------------------------------Heart Beat!!");
                          Serial.println(now_time);
                          prev_time = millis();
                          is_correct_count++;
                        }
		}

		if(acc_x-pre_x > 10 || acc_x-pre_x< -10) dis_x = acc_x-pre_x;
		else dis_x=0;

		if(acc_y-pre_y > 10 || acc_y-pre_y< -10) dis_y = acc_y-pre_y;		
		else dis_y=0;

		if(acc_y-pre_z > 10 || acc_y-pre_z< -10) dis_z = acc_y-pre_z;
		else dis_z=0;

		presure = analogRead(press_sen);
                Serial.print(now_heart);
		Serial.print(", ");
		Serial.print(presure);
		Serial.print(", ");
		Serial.print(dis_x);
		Serial.print(", ");
		Serial.print(dis_y);
		Serial.print(", ");
		Serial.println(dis_z);
		delay(3);	
                if(is_correct_count > reset_time) err_count = 0;	
	}
}
