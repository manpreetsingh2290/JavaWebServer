from tkinter import *
from urllib import parse, request
import socket

socket.setdefaulttimeout(30)

SERVER_URL = 'http://127.0.0.1:8081'

class Application(Frame):
    def __init__(self, master = None):
        Frame.__init__(self, None)

        self.pack()
        self.createWidgets()
        self.recordWidgets = []

    def createWidgets(self):
        self.getBtn = Button(self, text='Get', command = self.getBtn_control)
        self.getBtn.grid(row=0, column=0)
        self.modifyBtn = Button(self, text='Modify', command = self.modifyBtn_control)
        self.modifyBtn.grid(row=0, column=1)
        self.downloadBtn = Button(self, text='Download', command = self.downloadBtn_control)
        self.downloadBtn.grid(row=0, column=2)

        self.refreshBtn = Button(self, text='Refresh', command = self.refreshBtn_control)
        self.refreshBtn.grid(row=0, column=3)
        

    def getBtn_control(self):
        # refresh page
        self.refreshBtn_control()

        # get info from server
        infos = request.urlopen(SERVER_URL + '/classlist.txt').read().decode()

        records = []
        for info in infos.split('|'):
            tmp = info.split('#')
            if len(tmp) == 2:
                records.append(tmp)

        # display
        ri = 1
        for r in records:
            print(r)
            l = Label(self, text=r[0])
            l.grid(row=ri, column=0)
            e_str = StringVar()
            e = Entry(self, textvariable=e_str)
            e_str.set(r[1])
            e.grid(row=ri, column=1)

            self.recordWidgets.append( (l, e))
            ri += 1

    def modifyBtn_control(self):
        if self.recordWidgets is None:
            return
        new_vals = dict()
        for r in self.recordWidgets:
            new_vals[ r[0]['text'] ] = r[1].get()

        params = parse.urlencode(new_vals)

        request.urlopen(SERVER_URL + '/upload.html/modify?' + params)

    def downloadBtn_control(self):
        request.urlretrieve(SERVER_URL + '/classlist.txt', 'classlist.txt')

    def refreshBtn_control(self):
        for r in self.recordWidgets:
            r[0].destroy()
            r[1].destroy()

        self.recordWidgets = []
app = Application()
app.master.title('Manager System')
app.mainloop()
