class DeBrujin4:
    def __init__(self):
        self.state1 = 1
        self.state2 = 0
        self.state3 = 0
        self.state4 = 0

    def next(self):
        res = self.state4
        if self.state1 == 2 and self.state2 == 0 and self.state3 == 0 and self.state4 == 0:
            self.state1 = 0
            self.state2 = 0
            self.state3 = 0
            self.state4 = 0
        elif self.state1 == 0 and self.state2 == 0 and self.state3 == 0 and self.state4 == 0:
            self.state1 = 1
            self.state2 = 2
            self.state3 = 0
            self.state4 = 0
        else:
            self.state4 = self.state3
            self.state3 = self.state2
            self.state2 = self.state1
            self.state1 = (-2 * res + -2 * self.state4 + -2 * self.state3 + -2 * self.state2) % 5
        return res

class DeBrujin2:
    def __init__(self):
        self.state1 = 1
        self.state2 = 0
        self.state3 = 0
        self.state4 = 0

    def next(self):
        res = self.state4
        self.state4 = self.state3
        self.state3 = self.state2
        self.state2 = self.state1
        nor_val = (self.state2 + self.state3 + self.state4)
        self.state1 = (res + self.state2 + (1 if nor_val == 0 else 0)) % 2
        return res


machine = DeBrujin4()
machine2 = DeBrujin2()

f = open("de-brujin-sequence", "w")

for i in range(16 * 625 + 3):
    val1 = machine.next()
    val2 = machine2.next()
    next_num = val1 + 5 * val2
    f.write(f"{str(next_num)}")

f.close()
