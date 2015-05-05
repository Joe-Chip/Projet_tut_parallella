gcc -I /home/febroshka/Téléchargements/jdk1.7.0_75/include -I /jome/febroshka/Téléchargements/jdk1.7.0_75/include/linux/ -Wall -fPIC -c part2.c -o part2.o

gcc -shared part2.c -o libpart2.so

javac part2.java

java -Djava.library.path=. part2


