int s0 = A0;
int s1 = A1;
int s2 = A2;
float x, y, z,r;
void setup(){
  Serial.begin(9600);
}

void loop(){
  

  x = analogRead(s0);
  y = analogRead(s1);
  z = analogRead(s2);
  r=x*x+y*y+z*z;
  Serial.println(r);
 /* Serial.print(x);
  Serial.print(", ");
  Serial.print(y);
  Serial.print(", ");
  Serial.println(z);
  */
  delay(50);
}

