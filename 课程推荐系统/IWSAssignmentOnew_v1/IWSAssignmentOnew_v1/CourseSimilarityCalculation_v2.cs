using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace IWSAssignmentOnew_v1
{
    public class CourseSimilarityCalculation_v2
    {
        private Dictionary<int, double> courseSimilarityResult = new Dictionary<int, double>();
        public Dictionary<int, double> sortedSimilarityResult = new Dictionary<int, double>();
        /**
         */
        public int calculateSimilarity(Course newCourse, Dictionary<int, Course> courseDictionary)
        {
            //provide rate for different attribute
            double courseID_rate = 0.5;
            double lectureID_rate = 0.3;
            double tutorID_rate = 0.2;

            double currentScore = 0;
            //
            foreach (int courseID in courseDictionary.Keys)
            {
                currentScore = 0;
                //compare courseID attribute
                if (newCourse.courseID == courseDictionary[courseID].courseID)
                    currentScore = currentScore + courseID_rate;
                //compare lectureID attribute
                if (newCourse.lectureID == courseDictionary[courseID].lectureID)
                    currentScore = currentScore + lectureID_rate;
                //compare tutorID attribute
                if (newCourse.tutorID == courseDictionary[courseID].tutorID)
                    currentScore = currentScore + tutorID_rate;

                //calculate current course similarity
                this.courseSimilarityResult.Add(courseDictionary[courseID].id,currentScore);
            }

            //using Linq to sort similarity by score
            this.sortedSimilarityResult = (from d in this.courseSimilarityResult
                                           orderby d.Value descending
                                           select d).ToDictionary(pair => pair.Key, pair => pair.Value);

            //***
            //check if there is not any similar course exists
            foreach(int courseID in this.sortedSimilarityResult.Keys)
            {
                //if the score of first record is 0, that means cannot find any similar course
                if (this.sortedSimilarityResult[courseID] == 0.0)
                    return 0;
                else
                    break;
            }


            //If the some records have the same score, then have to consider the course average score
            int i = 1;
            double score = 0.0;
            int key = 0;
            String tFlag = "";
            foreach (int courseID in this.sortedSimilarityResult.Keys)
            {
                if (i == 1)
                {
                    score = this.sortedSimilarityResult[courseID];
                    key = courseID;
                }
                else
                {
                    if (score == this.sortedSimilarityResult[courseID])
                    {
                        tFlag = this.compareScore(newCourse, courseDictionary[key].averageScore, courseDictionary[courseID].averageScore);
                        //the second element more similar than first element
                        if (tFlag.Equals("2"))
                            key = courseID;
                    }
                    else
                        break;
                }
                i = i + 1;
            }
            return key;
        }
        /**
        */
        public String compareScore(Course newCourse, double score_1, double score_2)
        {
            if (Math.Abs(newCourse.averageScore - score_1) > Math.Abs(newCourse.averageScore - score_2))
                return "2";
            else
                return "1";
        }
    }
}