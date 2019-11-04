object p08 {
  def compressRecursive[A](ls: List[A]) : List[A] = ls match {
    case Nil => Nil
    case h::tail => h :: compressRecursive(tail.dropWhile(_ == h))
  }
}
