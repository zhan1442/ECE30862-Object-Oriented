#ifndef Set_H_
#define Set_H_
#include <list>
using namespace std;

class Set {
  list<int> nlist;
public:
  Set(int element) {nlist.push_back(element);}
  list<int> getnlist() {return nlist;}
  ~Set();
  // add(int element) {nlist.push_back(element);}
  
  void operator+(Set this, int newe) {
    this.getnlist().nlist.push_back(newe);
  }
}


ostream& operator<<(ostream& os, const Set set) {
  typedef list<string>::onst_iterator CI;
  for (CI iter = set.getnlist().begin(); iter != set.getnlist().end();iter++) {
    os << *iter << " "; 
  }
  os << endl;
}
#endif /*Set_H_*/
