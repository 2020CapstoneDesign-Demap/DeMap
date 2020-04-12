package kr.ac.hansung.demap

//어댑터에서 데이터 넘겨받기 위한 인터페이스
interface List_onClick_interface {
    fun onCheckbox(index: Int, position: Int)
}