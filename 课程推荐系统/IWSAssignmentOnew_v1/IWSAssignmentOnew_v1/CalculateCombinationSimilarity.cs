using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace IWSAssignmentOnew_v1
{
    public class CalculateCombinationSimilarity
    {
        //Define calculation weight for student and course similarity
        //(assume course similarity are more important than student similarity)
        private double student_weight = 0.4;
        private double course_weight = 0.6;

        //Define combine the students similarity variable.
        //choosing students whose similiarity score are equal or larger than this value
        private double thresHold { set; get; }
        private Dictionary<int, List<double>> courseSimiScoreListDictionary = new Dictionary<int, List<double>>();
        private Dictionary<int, double> courseSimiAverageDictionary = new Dictionary<int, double>();
        public Dictionary<int, double> sortedCourseSimiAverageDictionary = new Dictionary<int, double>();

        //final result
        private Dictionary<int, double> finalResultDictionary = new Dictionary<int, double>();
        public Dictionary<int, double> sortedFinalResultDictionary = new Dictionary<int, double>();

        /**
         */
        public void calCombinationSimilarity(Student newStudent, Dictionary<int, double> sortedStudentSimilarityResult,
            Dictionary<int, Student> studentDictionary, Dictionary<int, double> sortedPredictCourseRatingDic,
            double tThresHold, int executeSlopeOneFlag)
        {
            //set thresHold value
            this.thresHold = tThresHold;
            //------------------------------------------------------------------------------------------------
            //1. calculate course average rating score from different candidators
            //1.1 put course similarity score together 
            //????????????? if no similarity socre >= threshold
            foreach (int stuID in sortedStudentSimilarityResult.Keys)
            {
                //check the similarity score are equal or larger than thresHold
                if (sortedStudentSimilarityResult[stuID] < this.thresHold)
                    break;
                //
                foreach (int courseID in studentDictionary[stuID].sortedCourseRatingDic.Keys)
                {
                    //check whether the current courseID exists in interface input
                    if (newStudent.courseRatingDictionary.ContainsKey(courseID))
                        continue;
                    //
                    if (!this.courseSimiScoreListDictionary.ContainsKey(courseID))
                    {
                        //if current course is not rated, don't add into dictionary
                        if (studentDictionary[stuID].sortedCourseRatingDic[courseID] != 0)
                        {
                            List<double> tList = new List<double>();
                            this.courseSimiScoreListDictionary.Add(courseID, tList);
                            double curScore = sortedStudentSimilarityResult[stuID] * studentDictionary[stuID].sortedCourseRatingDic[courseID];
                            this.courseSimiScoreListDictionary[courseID].Add(curScore);
                        }
                    }
                    else
                    {
                        if (studentDictionary[stuID].sortedCourseRatingDic[courseID] != 0)
                        {
                            double curScore = sortedStudentSimilarityResult[stuID] * studentDictionary[stuID].sortedCourseRatingDic[courseID];
                            this.courseSimiScoreListDictionary[courseID].Add(curScore);
                        }
                    }
                }
            }

            //1.2 calculate course average rating score 
            double sumScore = 0.0;
            foreach(int courseID in this.courseSimiScoreListDictionary.Keys)
            {
                sumScore = 0.0;
                for (int i = 0; i < this.courseSimiScoreListDictionary[courseID].Count; i++)
                    sumScore = sumScore + this.courseSimiScoreListDictionary[courseID][i];
                this.courseSimiAverageDictionary.Add(courseID, sumScore / this.courseSimiScoreListDictionary[courseID].Count);
            }
            //1.3 using Linq to sort average rating score
            this.sortedCourseSimiAverageDictionary = (from d in this.courseSimiAverageDictionary
                                           orderby d.Value descending
                                           select d).ToDictionary(pair => pair.Key, pair => pair.Value);


            //------------------------------------------------------------------------------------------------
            //generate final similarity calculation results
            //if executeSlopeOneFlag equals 0, only consider student basic input information
            if (executeSlopeOneFlag == 0)
            {
                this.sortedFinalResultDictionary = this.sortedCourseSimiAverageDictionary;
                return;
            }
            //combine student and course information together to generate final result
            else
            {
                //calculate weighted student and course score to generate final score
                double curFinalScore = 0.0;
                foreach (int courseID in this.courseSimiAverageDictionary.Keys)
                {
                    curFinalScore = this.courseSimiAverageDictionary[courseID] * this.student_weight
                        + sortedPredictCourseRatingDic[courseID] * this.course_weight;
                    this.finalResultDictionary.Add(courseID, curFinalScore);
                }

                //Be careful the out loop should use sortedPredictCourseRatingDic, because courseSimiAverageDictionary may not 
                //include all the courses except the input courses from interface
                //foreach (int courseID in sortedPredictCourseRatingDic.Keys)
                //{
                //    //check whether courseSimiAverageDictionary contains current courseID
                //    if (this.courseSimiAverageDictionary.ContainsKey(courseID))
                //        curFinalScore = this.courseSimiAverageDictionary[courseID] * this.student_weight
                //        + sortedPredictCourseRatingDic[courseID] * this.course_weight;
                //    else
                //        curFinalScore = sortedPredictCourseRatingDic[courseID] * this.course_weight;
                    
                //    this.finalResultDictionary.Add(courseID, curFinalScore);
                //}
                //2.2 using Linq to sort final score
                this.sortedFinalResultDictionary = (from d in this.finalResultDictionary
                                                    orderby d.Value descending
                                                    select d).ToDictionary(pair => pair.Key, pair => pair.Value);
            }
        }
    }
}