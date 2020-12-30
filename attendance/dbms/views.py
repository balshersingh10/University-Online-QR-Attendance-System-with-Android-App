from django.db import connection
from django.contrib.auth import authenticate, login, logout
from django.urls import reverse
from django.contrib.auth.models import User, Group
from django.http import HttpResponse, HttpResponseRedirect
from django.contrib.auth.decorators import login_required
from datetime import datetime, timedelta, date
from dbms.forms import SignUpForm
from django.shortcuts import render, redirect
# Create your views here.

def index(request,error=None,message=None):
    print("1")
    iss = None
    if not request.user.is_authenticated:
        print("2")
        return render(request, "dbms/login_view.html" ,{"message":None})
    l=[]
    for g in request.user.groups.all():
        print("3")
        l.append(g.name)
    with connection.cursor() as cursor:
        print("4")
        if "instructor" in l:
            cursor.execute("SELECT course_id FROM public.teaches WHERE inst_id = %s",[request.user.username])
            row = [item[0] for item in cursor.fetchall()]
            context = {
                "user": request.user,
                "courses": row
            }
            return render(request , "dbms/instructor.html" , context)
        elif "student" in l:
            cursor.execute("SELECT course_id FROM public.takes WHERE stud_id = %s",[request.user.username])
            row = [item[0] for item in cursor.fetchall()]
            print(row)
            cursor.execute("SELECT * FROM stat WHERE student = %s",[request.user.username])
            att = cursor.fetchall()
            sort = sorted(att, key=lambda item: row.index(item[0]))
            attend = [s[4] for s in sort]
            total = [s[3] for s in sort]
            percent = ["{:.2f}".format((a/t)*100) if t!=0 else "-" for (a,t) in zip(attend,total)]
            l = zip(row,attend, total, percent)
            iss = False
            if row:
                iss = True
            context = {
                "user": request.user,
                "info": l,
                "error": error,
                "message": message,
                "iss":iss
            }
            print("QQQQQQQQQQQQQQQQQQQ")
            return render(request, "dbms/student.html" ,context)
        else:
            return render(request, "dbms/login_view.html" ,{"message":None})

def signup(request):
    if request.method == 'POST':
        form = SignUpForm(request.POST)
        if form.is_valid():
            with connection.cursor() as cursor:
                cursor.execute("SELECT setval('auth_user_id_seq', COALESCE((SELECT MAX(id)+1 FROM auth_user), 1), false)")
                form.save()
                username = form.cleaned_data.get('username')
                raw_password = form.cleaned_data.get('password1')
                user = authenticate(username=username, password=raw_password)
                group = form.cleaned_data.get('group')
                print("!@!@",group)
                group = Group.objects.get(name=group)
                user.groups.add(group)
                #login(request, user)
            return render(request, "dbms/login_view.html")
    else:
        form = SignUpForm()
    return render(request, 'dbms/signup.html', {'form': form})


def login_view(request):
    username = request.POST.get("username",False)
    password = request.POST.get("password",False)
    user = authenticate(request , username=username , password=password)
    if user is not None:
        login(request , user)
        return HttpResponseRedirect(reverse("index"))
    else:
        return render(request, "dbms/login_view.html" , {"message" : "Invalid Credentials."})

@login_required
def register(request):
    return render(request , "dbms/register.html")



@login_required
def register_course(request):
    code = request.POST.get("code", False)
    name = request.POST.get("name", False)
    with connection.cursor() as cursor:
        cursor.execute("SELECT id FROM public.course")
        row = [item[0] for item in cursor.fetchall()]
        if code in row:
            context={
            "message":"Course is already registered",
            "code"   : code
            }
            return render(request , "dbms/register.html" ,context)
        cursor.execute("INSERT INTO public.course VALUES (%s, %s)",[code,name])
        cursor.execute("INSERT INTO public.teaches VALUES (%s, %s)",[request.user.username,code])
        cursor.execute("SELECT course_id FROM public.teaches WHERE inst_id = %s",[request.user.username])
        row = [item[0] for item in cursor.fetchall()]
        context = {
            "message":"Course Registered Successfully",
            "courses": row
        }
        return render(request, "dbms/instructor.html", context)

@login_required
def drop(request,course):
    with connection.cursor() as cursor:
        cursor.execute("DELETE FROM course WHERE id = %s",[course])
        return index(request)


@login_required
def enrollc(request):
    if request.method == 'POST':
        code = request.POST.get("code", False)
        error = None
        message = None
        with connection.cursor() as cursor:
            cursor.execute("SELECT id FROM course WHERE id = %s",[code])
            row = [item[0] for item in cursor.fetchall()]
            if not row:
                error = "No such Course found"
                return index(request,error,message)
            cursor.execute("SELECT * from takes where stud_id=%s and course_id = %s",[request.user.username,code])
            row = [item[0] for item in cursor.fetchall()]
            if row:
                error = "Already Registered in this Course"
                return index(request,error,message)
            cursor.execute("INSERT into public.takes VALUES (%s,%s) ON CONFLICT DO NOTHING",[request.user.username,code])
            message = "Successfully Registered in Course"
            print("aaaaa",message)
            return index(request,error,message)
    print("oooooooooooooooooooooo")
    return render(request, "dbms/enroll.html")

@login_required
def dereg(request,course):
    with connection.cursor() as cursor:
        cursor.execute("DELETE from takes where stud_id=%s and course_id=%s",[request.user.username,course])
        cursor.execute("SELECT class_id from attendance,class where attendance.class_id=class.id and course_id=%s and stud_id=%s",[course,request.user.username])
        row = [item[0] for item in cursor.fetchall()]
        if row:
            for i in range(0,len(row)):
                cursor.execute("DELETE from attendance where class_id=%s and stud_id=%s",[row[i],request.user.username])
        message = "Successfully Deregistered from course"
        return index(request,message=message)


@login_required
def course_detail(request,course):
    print("!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    with connection.cursor() as cursor:
        cursor.execute("SELECT stud_id FROM public.takes WHERE course_id = %s",[course])
        row = [item[0] for item in cursor.fetchall()]
        cursor.execute("SELECT * FROM stat WHERE course = %s",[course])
        att = cursor.fetchall()
        sort = sorted(att, key=lambda item: row.index(item[1]))
        attend = [s[4] for s in sort]
        total = [s[3] for s in sort]
        percent = ["{:.2f}".format((a/t)*100) if t!=0 else "-" for (a,t) in zip(attend,total)]
        l = zip(row,attend, total, percent)
        context = {
            "user" : request.user,
            "students"  : row,
            "course_id" : course,
            "mylist" : l
        }
        return render(request , "dbms/course_detail.html", context)

@login_required
def qr(request,course):
    times = request.POST.get("times", False)
    times = int(times)
    today = date.today()
    today = today.strftime("%Y-%m-%d")
    with connection.cursor() as cursor:
        cursor.execute("SELECT * FROM public.class WHERE inst_id = %s and course_id = %s and date = %s and times = %s",[request.user.username,course,today,times])
        row = cursor.fetchall()
        print("1",row)
        if row:
            for item in row:
                print(item)
                i, i_id, c_id, d, t,tt = item
                qrcode = str(i)+"$"+i_id+"$"+c_id+"$"+str(d)+"$"+str(t)+"$"+str(tt)
                break
        else:
            cursor.execute("SELECT MAX(id) FROM public.class")
            row2 = cursor.fetchone()
            print("2",row2)
            if row2[0]==None:
                i = 1
            else:
                row2 = row2[0]
                i = int(row2)+1
            i_id = request.user.username
            c_id = course
            d = today
            now = datetime.now()
            current_time = now.strftime("%H:%M:%S")
            t = current_time
            tt = times
            qrcode = str(i)+"$"+i_id+"$"+c_id+"$"+d+"$"+t+"$"+str(tt)
            cursor.execute("INSERT INTO public.class VALUES (%s,%s,%s,%s,%s,%s)",[i,i_id,c_id,d,t,tt])
        cursor.execute("SELECT stud_id FROM public.takes WHERE course_id = %s",[course])
        row = [item[0] for item in cursor.fetchall()]
        cursor.execute("SELECT * FROM stat WHERE course = %s",[course])
        att = cursor.fetchall()
        sort = sorted(att, key=lambda item: row.index(item[1]))
        attend = [s[4] for s in sort]
        total = [s[3] for s in sort]
        percent = ["{:.2f}".format((a/t)*100) for (a,t) in zip(attend,total)]
        l = zip(row,attend, total, percent)
        context = {
            "user" : request.user,
            "students"  : row,
            "course_id" : course,
            "qr" : qrcode,
            "mylist":l
        }
        return render(request , "dbms/course_detail.html", context)

@login_required
def mark_attendence(request , course):
    times = request.POST.get("times", False)
    times = int(times)
    today = date.today()
    today = today.strftime("%Y-%m-%d")
    class_id = None
    with connection.cursor() as cursor:
        cursor.execute("SELECT * FROM public.class WHERE inst_id = %s and course_id = %s and date = %s and times = %s",[request.user.username,course,today,times])
        row = cursor.fetchall()
        if row:
            for item in row:
                i, i_id, c_id, d, t,tt = item
                class_id = i
                break
        else:
            cursor.execute("SELECT MAX(id) FROM public.class")
            row2 = cursor.fetchone()
            if row2[0]==None:
                i = 1
            else:
                row2 = row2[0]
                i = int(row2)+1
            i_id = request.user.username
            c_id = course
            d = today
            now = datetime.now()
            current_time = now.strftime("%H:%M:%S")
            t = current_time
            tt = times
            qrcode = str(i)+"$"+i_id+"$"+c_id+"$"+d+"$"+t+"$"+str(tt)
            cursor.execute("INSERT INTO public.class VALUES (%s,%s,%s,%s,%s,%s)",[i,i_id,c_id,d,t,tt])
            class_id = i
        cursor.execute("SELECT stud_id FROM public.takes WHERE course_id = %s",[course])
        row = [item[0] for item in cursor.fetchall()]
        for s in row:
            p = request.POST.get(s,False)
            if p=="Present":
                cursor.execute("INSERT INTO public.attendance VALUES (%s,%s) ON CONFLICT DO NOTHING",[i,s])
        cursor.execute("SELECT * FROM stat WHERE course = %s",[course])
        att = cursor.fetchall()
        sort = sorted(att, key=lambda item: row.index(item[1]))
        attend = [s[4] for s in sort]
        total = [s[3] for s in sort]
        percent = ["{:.2f}".format((a/t)*100) for (a,t) in zip(attend,total)]
        l = zip(row,attend, total, percent)
        context = {
            "user" : request.user,
            "students"  : row,
            "course_id" : course,
            "mylist":l
        }
        return render(request , "dbms/course_detail.html", context)

def logout_view(request):
    logout(request)
    return render(request , "dbms/login_view.html", {"message" : "Logged out!"})
