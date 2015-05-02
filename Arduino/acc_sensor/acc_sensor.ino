int s0 = A0;
int s1 = A1;
int s2 = A2;

int pre_x, pre_y, pre_z,r_x, r_y, r_z;

int x =0;
int y = 0;
int z = 0;

void setup(){
  Serial.begin(9600);
}

void loop(){
  pre_x = x;
  pre_y = y;
  pre_z = z;
  
  
  x = analogRead(s0);
  y = analogRead(s1);
  z = analogRead(s2);
  
  if(x-pre_x > 10 || x-pre_x< -10){
    r_x = x-pre_x;
  }
  else r_x=0;

  if(y-pre_y > 10 || y-pre_y< -10){
    r_y = y-pre_y;
  }
  else r_y=0;

  if(z-pre_z > 10 || z-pre_z< -10){
    r_z = z-pre_z;
  }
  else r_z=0;

  
 // r=x*x+y*y+z*z;
 //Serial.println(r);
  Serial.print(r_x);
  Serial.print(", ");
  Serial.print(r_y);
  Serial.print(", ");
  Serial.println(r_z);
 
  delay(50);
}

