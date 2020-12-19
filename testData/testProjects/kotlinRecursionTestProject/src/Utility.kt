fun factorial(num: Int): Long {
    if (num >= 1)
        return num*factorial(num-1)
    else
        return 1
}