#include "Set.h"
#include <iostream>
#include <string>
using namespace std;

int main(int argc, char * argv[]) {

   Set a = Set(63);
   Set b = Set(63);

   a = a + 4;
   a = a + 8;
   cout << "a w/4, 8: " << a << endl;

   b = b + 8;
   b = b + 16;
   cout << "b w/8, 16: " << b << endl;

   a = a / b;
   cout << "a / b: " << a << endl;

   a = a + 12;
   a = a + 16;
   //a = a - 12;
   //a = a - 7;
   cout << "a w/4, 16: " << a << endl;

   cout << "~a: " << ~a << endl;
   cout << "a copy count: " << a.getCopyCount( ) << endl;
   cout << "b copy count: " << b.getCopyCount( ) << endl;
}

class Set {
  list<int> nlist;
public:
  Set(int element) {nlist.push_back(element);}
  list<int> getnlist() {return nlist;}
  ~Set();
  // add(int element) {nlist.push_back(element);}
  
  Operator+(Set set, int newe) {
    nlist.push_back(newe);
  }
}

ostream& Operator<<(ostream& os, const Set& set) {
  typedef list<string>::onst_iterator CI;
  for (CI iter = set.getnlist().begin(); iter != set.getnlist().end();iter++) {
    os << *iter << " "; 
  }
  os << endl;
}
