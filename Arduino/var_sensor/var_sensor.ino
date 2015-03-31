int Var_pin=A0;
int var_result;

void setup() {
  Serial.begin(9600);
}

void loop() {
  var_result=analogRead(Var_pin);
  Serial.println(var_result);
  delay(20);
}
