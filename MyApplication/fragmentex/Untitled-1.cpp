#include <iostream>
using namespace std;

int main() {
    char input;
    int l_count = 0, o_count = 0, v_count = 0, e_count = 0;

    while (cin.get(input)) {
        if (input == EOF) {
            break;
        }
        // l, o, v, e의 개수를 센다
        if (input == 'l' || input == 'L') {
            l_count++;
        } else if (input == 'o' || input == 'O') {
            o_count++;
        } else if (input == 'v' || input == 'V') {
            v_count++;
        } else if (input == 'e' || input == 'E') {
            e_count++;
        }
    }

    cout << "알파벳 l의 개수: " << l_count << endl;
    cout << "알파벳 o의 개수: " << o_count << endl;
    cout << "알파벳 v의 개수: " << v_count << endl;
    cout << "알파벳 e의 개수: " << e_count << endl;

    return 0;
}