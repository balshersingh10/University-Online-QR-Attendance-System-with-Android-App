from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view
from django.db import connection
from django.contrib.auth.models import User , Group
from demo.api.serializers import *
from django.contrib.auth.hashers import make_password, check_password
from datetime import *

@api_view(['GET', 'POST'])
def checkuser(request):
    if request.method == 'POST':
        serializer = UserSerializer(data=request.data)
        if serializer.is_valid():
            username = serializer.data["username"]
            password = serializer.data["password"]
            with connection.cursor() as cursor:
                cursor.execute("SELECT A.password FROM public.auth_user as A, public.auth_group as B, public.auth_user_groups as C WHERE A.id = C.user_id and C.group_id = B.id and A.username = %s and B.name = %s",[username,'student'])
                row = cursor.fetchone()
                print(row)
                if row == None or not check_password(password,row[0]):
                    response_serializer = UserResponse(data={'flag':'Invalid Credentials'})
                    if response_serializer.is_valid():
                        return Response(response_serializer.data, status=status.HTTP_200_OK)
                    else:
                        return Response(response_serializer.errors, status=status.HTTP_400_BAD_REQUEST)
                else:
                    response_serializer = UserResponse(data={'flag':'correct'})
                    if response_serializer.is_valid():
                        return Response(response_serializer.data, status=status.HTTP_200_OK)
                    else:
                        return Response(response_serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET','POST'])
def viewattendance(request):
    if request.method == 'POST':
        serializer = AttendanceSerializer(data=request.data)
        if serializer.is_valid():
            username = serializer.data["username"]
            course_id = serializer.data["course_id"]
            with connection.cursor() as cursor:
                cursor.execute("SELECT * from stat WHERE student = %s and course = %s",[username,course_id])
                row = cursor.fetchone()
                response_serializer = AttendanceResponse(data={'total_classes':row[3],'total_attended':row[4]})
                if response_serializer.is_valid():
                    return Response(response_serializer.data, status=status.HTTP_200_OK)
                else:
                    return Response(response_serializer.errors, status=status.HTTP_400_BAD_REQUEST)
                    '''
                cursor.execute("select * from student_attendance where course_id1=%s and stud_id=%s",[course_id,username])
                if(not cursor.fetchone()):
                    classes_attended = '0'
                else:
                    cursor.execute("select * from student_attendance where course_id1=%sand stud_id=%s",[course_id,username])
                    classes_attended = cursor.fetchone()[4]
                cursor.execute("select * from student_attendance where course_id1=%sand stud_id=%s",[course_id,username])
                if(not cursor.fetchone()):
                    total_classes = '0'
                else:
                    cursor.execute("select * from student_attendance where course_id1=%sand stud_id=%s",[course_id,username])
                    total_classes = cursor.fetchone()[1]
                if classes_attended != None and total_classes!= None:
                    response_serializer = AttendanceResponse(data={'total_classes':total_classes,'total_attended':classes_attended})
                    if response_serializer.is_valid():
                        return Response(response_serializer.data, status=status.HTTP_200_OK)
                    else:
                        return Response(response_serializer.errors, status=status.HTTP_400_BAD_REQUEST)
                else:
                    response_serializer = AttendanceResponse(data={'total_classes':'0','total_attended':'0'})
                    if response_serializer.is_valid():
                        return Response(response_serializer.data, status=status.HTTP_200_OK)
                    else:
                        return Response(response_serializer.errors, status=status.HTTP_400_BAD_REQUEST)'''


@api_view(['GET', 'POST'])
def getallcourses(request):
    if request.method == 'POST':
        with connection.cursor() as cursor:
            cursor.execute("select * from public.course")
            row1 = [item[1] for item in cursor.fetchall()]
            cursor.execute("select * from public.course")
            row0 = [item[0] for item in cursor.fetchall()]
            #print(row1)
            if not row0:
                course_id = '-1'
            else:
                course_id = "-".join(row0)

            if not row1:
                course_title = '-1'
            else:
                course_title = "-".join(row1)
            #print(">>>>",course_id)
            #print(">>>>",course_title)
            response_serializer = AllCoursesResponse(data={'course_id':course_id,'course_title':course_title})
            if response_serializer.is_valid():
                return Response(response_serializer.data, status=status.HTTP_200_OK)
            else:
                return Response(response_serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET','POST'])
def signup(request):
    if request.method == 'POST':
        serializer = SignUpSerializer(data=request.data)
        if serializer.is_valid():
            username = serializer.data["username"]
            password = serializer.data["password"]
            encr_password = make_password(password)
            fname = serializer.data["fname"]
            lname = serializer.data["lname"]
            email = serializer.data["email"]
            is_superuser = False
            is_staff = False
            is_active = True
            date_joined = datetime.now()
            with connection.cursor() as cursor:
                cursor.execute("select * from public.auth_user where username=%s",[username])
                conf = cursor.fetchone()
                if(conf != None):
                    response_serializer = SignUpResponse(data={'flag':'Username already Exists'})
                    if response_serializer.is_valid():
                        return Response(response_serializer.data, status=status.HTTP_200_OK)
                    else:
                        return Response(response_serializer.errors, status=status.HTTP_400_BAD_REQUEST)
                cursor.execute("select * from public.auth_user where email=%s",[email])
                conf = cursor.fetchone()
                if(conf != None):
                    response_serializer = SignUpResponse(data={'flag':'This Email is Already Registered'})
                    if response_serializer.is_valid():
                        return Response(response_serializer.data, status=status.HTTP_200_OK)
                    else:
                        return Response(response_serializer.errors, status=status.HTTP_400_BAD_REQUEST)
                else:
                    cursor.execute("select max(id) from public.auth_user")
                    id1 = cursor.fetchone()
                    cursor.execute("select max(id) from public.auth_user_groups")
                    id2 = cursor.fetchone()
                    cursor.execute("INSERT INTO public.auth_user(id,password,username,first_name,last_name,email,is_superuser,is_staff,is_active,date_joined) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) ON CONFLICT DO NOTHING",[(int(id1[0]) + 1),encr_password,username,fname,lname,email,is_superuser,is_staff,is_active,date_joined])
                    cursor.execute("Insert into public.auth_user_groups values (%s,%s,%s) ON CONFLICT DO NOTHING",[(int(id2[0]) + 1),(int(id1[0]) + 1),2])
                    response_serializer = SignUpResponse(data={'flag':'Successfully Registered'})
                    if response_serializer.is_valid():
                        return Response(response_serializer.data, status=status.HTTP_200_OK)
                    else:
                        return Response(response_serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET','POST'])
def deregister(request):
    if request.method == 'POST':
        serializer = DeregisterSerializer(data=request.data)
        if serializer.is_valid():
            username = serializer.data["username"]
            course_id = serializer.data["course_id"]
            with connection.cursor() as cursor:
                cursor.execute("DELETE from takes where stud_id=%s and course_id=%s",[username,course_id])
                cursor.execute("SELECT class_id from attendance,class where attendance.class_id=class.id and course_id=%s and stud_id=%s",[course_id,username])
                row = [item[0] for item in cursor.fetchall()]
                print(row)
                if(not row):
                    stat = "No issue"
                else:
                    for i in range(0,len(row)):
                        cursor.execute("DELETE from attendance where class_id=%s and stud_id=%s",[row[i],username])
                response_serializer = DeregisterResponse(data={'message':'De-Registered successfully'})
                if response_serializer.is_valid():
                    return Response(response_serializer.data, status=status.HTTP_200_OK)
                else:
                    return Response(response_serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET', 'POST'])
def getcourse(request):
    if request.method == 'POST':
        serializer = CourseSerializer(data=request.data)
        if serializer.is_valid():
            username = serializer.data["username"]
            with connection.cursor() as cursor:
                cursor.execute("SELECT course_id FROM public.takes WHERE stud_id = %s",[username])
                row = [item[0] for item in cursor.fetchall()]
                if not row:
                    courses = '-1'
                else:
                    courses = "-".join(row)
                response_serializer = CourseResponse(data={'courses':courses})
                if response_serializer.is_valid():
                    return Response(response_serializer.data, status=status.HTTP_200_OK)
                else:
                    return Response(response_serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET', 'POST'])
def enroll(request):
    if request.method == 'POST':
        serializer = EnrollSerializer(data=request.data)
        if serializer.is_valid():
            username = serializer.data["username"]
            course_id = serializer.data["course_id"]
            with connection.cursor() as cursor:
                cursor.execute("SELECT * from takes where stud_id=%s and course_id = %s",[username,course_id])
                if(not cursor.fetchone()):
                    stat = "No problem with previous courses"
                else:
                    response = EnrollResponse(data={'message':'Already Enrolled in'})
                    if response.is_valid():
                        return Response(response.data, status=status.HTTP_200_OK)
                    else:
                        return Response(response.errors, status=status.HTTP_400_BAD_REQUEST)
                cursor.execute("SELECT id FROM course WHERE id = %s",[course_id])
                row = [item[0] for item in cursor.fetchall()]
                if not row:
                    response = EnrollResponse(data={'message':'No such Course found'})
                    if response.is_valid():
                        return Response(response.data, status=status.HTTP_200_OK)
                    else:
                        return Response(response.errors, status=status.HTTP_400_BAD_REQUEST)
                else:
                    cursor.execute("INSERT into public.takes VALUES (%s,%s) ON CONFLICT DO NOTHING",[username,course_id])
                    response = EnrollResponse(data={'message':'Successfully Registerd into Course'})
                    if response.is_valid():
                        return Response(response.data, status=status.HTTP_200_OK)
                    else:
                        return Response(response.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET', 'POST'])
def qrcode(request):
    if request.method == 'POST':
        serializer = QRSerializer(data=request.data)
        if serializer.is_valid():
            username = serializer.data["username"]
            qrcode = serializer.data["qr"]
            try:
                i, i_id, c_id, d, t, tt = qrcode.split("$")
            except:
                response = QRResponse(data={'message':'Invalid class'})
                if response.is_valid():
                    return Response(response.data, status=status.HTTP_200_OK)
                else:
                    return Response(response.errors, status=status.HTTP_400_BAD_REQUEST)
            with connection.cursor() as cursor:
                cursor.execute("SELECT * FROM public.class WHERE id = %s and inst_id = %s and course_id = %s and date = %s and class.time = %s and class.times = %s",[i, i_id, c_id, d, t, tt])
                row = [item[0] for item in cursor.fetchall()]
                cursor.execute("SELECT * FROM public.takes WHERE stud_id = %s and course_id = %s",[username,c_id])
                row2 = [item[0] for item in cursor.fetchall()]
                now = datetime.now()
                current_time = now.strftime("%H:%M:%S")
                origin = datetime.combine(datetime.strptime(d,"%Y-%m-%d").date(),datetime.strptime(t,"%H:%M:%S").time())
                flag = False
                if (now-origin).seconds >1800:
                    flag = True
                if not row or not row2 or flag:
                    response = QRResponse(data={'message':'Invalid class'})
                    if response.is_valid():
                        return Response(response.data, status=status.HTTP_200_OK)
                    else:
                        return Response(response.errors, status=status.HTTP_400_BAD_REQUEST)
                else:
                    cursor.execute("INSERT into public.attendance VALUES (%s,%s) ON CONFLICT DO NOTHING",[i,username])
                    response = QRResponse(data={'message':'Attendence marked Successfully'})
                    if response.is_valid():
                        return Response(response.data, status=status.HTTP_200_OK)
                    else:
                        return Response(response.errors, status=status.HTTP_400_BAD_REQUEST)
