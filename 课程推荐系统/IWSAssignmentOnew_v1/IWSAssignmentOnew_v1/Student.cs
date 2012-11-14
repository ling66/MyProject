using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace IWSAssignmentOnew_v1
{
    public class Student
    {
        //Properties
        public int id { get; set; }
        public int education { get; set; }
        public int sex { get; set; }
        public int type { get; set; }
        public int GPA { get; set; }

        public Dictionary<int, int> courseRatingDictionary = new Dictionary<int, int>();
        public Dictionary<int, int> sortedCourseRatingDic = new Dictionary<int, int>();

        public void sortedCourseRating()
        {
            //using Linq to sort by Rating Value
            this.sortedCourseRatingDic = (from d in this.courseRatingDictionary
                                          orderby d.Value descending
                                          select d).ToDictionary(pair => pair.Key, pair => pair.Value);
        }

        public override string ToString()
        {
            return string.Format("id:{0} education:{1} sex:{2} type:{3} GPA:{4}", id, education, sex, type, GPA);
        }
    }
}