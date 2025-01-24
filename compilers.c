#include "stdio.h"
#include "stdlib.h"
#include "string.h"

char *tempBuffer;
int somma(int a, int b);
void init_globalString(){
}
int somma(int a, int b) {
return a + b;
}
int main() { 
init_globalString();
int x, y;
scanf(" %d", & x);
scanf(" %d", & y);
somma(x, y);
printf("%s %d \n","Il valore della somma è \0", somma(x, y));
}
