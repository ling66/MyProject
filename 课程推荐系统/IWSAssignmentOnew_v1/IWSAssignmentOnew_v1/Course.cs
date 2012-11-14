using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Text;

namespace IWSAssignmentOnew_v1
{
    public class Course
    {
        //Properties
        public int id { get; set; }
        public int courseID { get; set; }
        public int lectureID { get; set; }
        public int tutorID { get; set; }
        public int type { get; set; }
        public List<int> studentList = new List<int>();
        public double averageScore { get; set; }
        //public int student_1 { get; set; }
        //public int student_2 { get; set; }
        //public int student_3 { get; set; }
        //public int student_4 { get; set; }
        //public int student_5 { get; set; }
        //public int student_6 { get; set; }
        //public int student_7 { get; set; }
        //public int student_8 { get; set; }
        //public int student_9 { get; set; }
        //public int student_10 { get; set; }

        public override string ToString()
        {
            StringBuilder tReturnStr = new StringBuilder();
            tReturnStr.Append(string.Format("id:{0} courseID:{1} lectureID:{2} tutorID:{3} type:{4} "
                , id, courseID, lectureID, tutorID, type));

            for (int i = 0; i < studentList.Count; i++)
                tReturnStr.Append(string.Format("student_{0}:{1} ", i + 1, studentList[i]));
            tReturnStr.Append(string.Format("AverageScore:{0}",averageScore));

            return tReturnStr.ToString();
        }
    }
}