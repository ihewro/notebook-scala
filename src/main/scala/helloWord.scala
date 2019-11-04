object helloWord {
  def main(args: Array[String]):Unit= {
    val list = List(1, 2)
    println(list.length)
    /*list match {
      // 表示列表只有两个元素
      case h :: e :: Nil =>
        println (e);
      case _ => println("no")
    }*/

    val test = List(1,2,2,1)
  println(isPalindrome(test))
  }

  def isPalindrome[A](ls: List[A]): Boolean = ls == ls.reverse

}
