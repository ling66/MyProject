using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Diagnostics;
using System.IO;
using System.Data;

namespace IWSAssignmentOnew_v1
{
    public partial class StudentInfoInputForm : System.Web.UI.Page
    {
        //Properties
        private String studentsInfoFilePath
        {
            get { return tbFileUploadPath.Text+"\\student.csv"; }
            //get { return "C:/Users/LL/Desktop/student.csv"; }
        }
        private String coursesInfoFilePath
        {
            get { return tbFileUploadPath.Text+"\\course.csv"; }
            //get { return "C:/Users/LL/Desktop/course.csv"; }
        }

        //store students information provided by student information file
        //private Dictionary<int, Student> studentDictionary = new Dictionary<int, Student>();
        private Dictionary<int, Student> studentDictionary = null;
        //store courses information provided by course information file
        //private Dictionary<int, Course> courseDictionary = new Dictionary<int, Course>();
        private Dictionary<int, Course> courseDictionary = null;

        //this flag use for check whether to execute slope one algorithm
        // 0:don't execute; 1:execute
        private int executeSlopeOneFlag = 1;
        //store the maximum course ID
        private int maxCourseID = 0;
        //store the completed courses
        private List<int> completedCoursesList = null;

        protected void Page_Load(object sender, EventArgs e)
        {
            Debug.WriteLine("------- Program Start --------");
            //Debug.Write("aa");
        }
        /*
         * validate input from interface
         * if user choose any course, he/she also has to input lecturer and tutor information
         * */
        private Boolean validationInput()
        {
            if (!ddlRate1.SelectedValue.Equals("NOT COMPLETED"))
            {
                if (tbLecturer1.Text.Equals("") || tbTutor1.Text.Equals(""))
                {
                    lbMessage.Text = "The Lecturer or Tutor field cannot be empty!";
                    return false;
                }
            }
            if (!ddlRate2.SelectedValue.Equals("NOT COMPLETED"))
            {
                if (tbLecturer2.Text.Equals("") || tbTutor2.Text.Equals(""))
                {
                    lbMessage.Text = "The Lecturer or Tutor field cannot be empty!";
                    return false;
                }
            }
            if (!ddlRate3.SelectedValue.Equals("NOT COMPLETED"))
            {
                if (tbLecturer3.Text.Equals("") || tbTutor3.Text.Equals(""))
                {
                    lbMessage.Text = "The Lecturer or Tutor field cannot be empty!";
                    return false;
                }
            }
            if (!ddlRate4.SelectedValue.Equals("NOT COMPLETED"))
            {
                if (tbLecturer4.Text.Equals("") || tbTutor4.Text.Equals(""))
                {
                    lbMessage.Text = "The Lecturer or Tutor field cannot be empty!";
                    return false;
                }
            }
            return true;
        }
        /*
         * submit button click
         * */
        protected void Button1_Click(object sender, EventArgs e)
        {
            //Interface Input validation 
            if (!this.validationInput())
                return;
            lbMessage.Text = "";

            //***
            //Reset Variables for each click(Be careful with global variables)
            this.studentDictionary = new Dictionary<int, Student>();
            this.courseDictionary = new Dictionary<int, Course>();
            this.executeSlopeOneFlag = 1;
            maxCourseID = 0;
            completedCoursesList = new List<int>();
            //Reset all the table DataSource
            gvCourseInfo.DataSource = null;
            gvCourseInfo.DataBind();
            gvCoursePreRating.DataSource = null;
            gvCoursePreRating.DataBind();
            gvCourseSimilarity.DataSource = null;
            gvCourseSimilarity.DataBind();
            gvFinalResult.DataSource = null;
            gvFinalResult.DataBind();
            gvStuAttrWeight.DataSource = null;
            gvStuAttrWeight.DataBind();
            gvStudentInfo.DataSource = null;
            gvStudentInfo.DataBind();
            gvStudentsRating.DataSource = null;
            gvStudentsRating.DataBind();
            gvSumStudentCourseRating.DataSource = null;
            gvSumStudentCourseRating.DataBind();

            //-----------------------------------------------------------------------------------
            //---------------------- Reading Data From files ------------------------------------
            //-----------------------------------------------------------------------------------
            //1. Read student and course information from given files
            //*** 
            //checking file exists
            if (!this.readStudentsInfo())
                return;
            if (!this.readCoursesInfo())
                return;
            //2. sort course rating score for each student from training data set
            this.readStudentSortedRatingForCourses();

            //-----------------------------------------------------------------------------------
            //---------------------- Reading Data From Interface --------------------------------
            //-----------------------------------------------------------------------------------
            //1. Read input new student information
            Student newStudent = new Student();
            this.readInputStudentInfo(newStudent);

            //2. Read input course infromation 
            string[,] coureSimilarityTable = this.readInputCourseInfo(newStudent);
            //if coureSimilarityTable = null
            //that means there is no any course information input or can not find any similar course
            if (coureSimilarityTable == null)
                this.executeSlopeOneFlag = 0;

            //-----------------------------------------------------------------------------------
            //---------------------- Executing Different Algorithm Parts  -----------------------
            //-----------------------------------------------------------------------------------
            //***
            //thinking about which algorithm should be run
            //1. Calculate similarity form student input information(this part has to be executed at any time)
            StudentSimilarityCalculation_v3 tStudentSimilarityCalculation = new StudentSimilarityCalculation_v3();
            tStudentSimilarityCalculation.calculateSimilarity(newStudent, this.studentDictionary, this.courseDictionary);

            //2. Calculate similarity form course input information(this part is optional according to conditiions)
            //   Use Slope One Algorithm to calculate course rating predication
            SlopeOneCalculation tSlopeOneCalculation = new SlopeOneCalculation();
            if (this.executeSlopeOneFlag != 0)
            {
                tSlopeOneCalculation.predictRatingForOtherCourses(this.courseDictionary, newStudent);
            }

            //3. Generate final combination results
            CalculateCombinationSimilarity tCalculateCombinationSimilarity = new CalculateCombinationSimilarity();
            tCalculateCombinationSimilarity.calCombinationSimilarity(newStudent, tStudentSimilarityCalculation.sortedSimilarityResult,
            this.studentDictionary, tSlopeOneCalculation.sortedPredictCourseRatingDic, 
            tStudentSimilarityCalculation.thresHold,this.executeSlopeOneFlag);

            //4. choose three courses which gain the highest score from final results set
            //   also consider that the recommended subjects are not below the level of completed courses
            string[,] finalRecomResultTable = new string[1, 3];
            int countNum = 0;
            foreach (int courseID in tCalculateCombinationSimilarity.sortedFinalResultDictionary.Keys)
            {
                if ( (this.courseDictionary[courseID].courseID <= this.maxCourseID)
                    || this.completedCoursesList.Contains(this.courseDictionary[courseID].courseID))
                    continue;
                if (countNum > 2)
                    break;
                finalRecomResultTable[0, countNum] = Convert.ToString(String.Format("{0}(id:{1})-{2:0.####}",
                    this.courseDictionary[courseID].courseID, courseID, tCalculateCombinationSimilarity.sortedFinalResultDictionary[courseID]));
                countNum = countNum + 1;
            }
            string[] finalResultRecomTableColName = new string[3] { "Recommand Course_1", "Recommand Course_2", "Recommand Course_3" };
            gvFinalRecomCourse.DataSource = ConvertToDataTable(finalResultRecomTableColName, finalRecomResultTable);
            gvFinalRecomCourse.DataBind();

            //----------------------------------------------------------------------------------
            //---------------------- Display Some Calculation Result  --------------------------
            //----------------------------------------------------------------------------------
            //1. display student infromation
            //1.1 build student information array
            string[,] studentInforTable = new string[this.studentDictionary.Count,5];
            int r_1_1 = 0;
            foreach (int stuID in this.studentDictionary.Keys)
            { 
                studentInforTable[r_1_1,0]=Convert.ToString(stuID);
                studentInforTable[r_1_1,1]=Convert.ToString(this.studentDictionary[stuID].education);
                studentInforTable[r_1_1, 2] = Convert.ToString(this.studentDictionary[stuID].sex);
                studentInforTable[r_1_1, 3] = Convert.ToString(this.studentDictionary[stuID].type);
                studentInforTable[r_1_1, 4] = Convert.ToString(this.studentDictionary[stuID].GPA);
                r_1_1 = r_1_1 + 1;
            }
            //1.2 build table column Name
            string[] stuInfoTableColName = new string[5];
            stuInfoTableColName[0] = "StudentID";
            stuInfoTableColName[1] = "pg/ug";
            stuInfoTableColName[2] = "m/f";
            stuInfoTableColName[3] = "int/local";
            stuInfoTableColName[4] = "GPA";
            gvStudentInfo.DataSource = ConvertToDataTable(stuInfoTableColName, studentInforTable);
            gvStudentInfo.DataBind();

            //2. display course information
            //2.1 build course information array
            string[,] courseInforTable = new string[this.courseDictionary.Count, 16];
            int r_2_1 = 0;
            foreach (int id in this.courseDictionary.Keys)
            {
                courseInforTable[r_2_1, 0] = Convert.ToString(id);
                courseInforTable[r_2_1, 1] = Convert.ToString(this.courseDictionary[id].courseID);
                courseInforTable[r_2_1, 2] = Convert.ToString(this.courseDictionary[id].lectureID);
                courseInforTable[r_2_1, 3] = Convert.ToString(this.courseDictionary[id].tutorID);
                courseInforTable[r_2_1, 4] = Convert.ToString(this.courseDictionary[id].type);
                for (int i = 0; i < this.courseDictionary[id].studentList.Count; i++)
                {
                    courseInforTable[r_2_1, i + 5] = Convert.ToString(this.courseDictionary[id].studentList[i]);
                }
                courseInforTable[r_2_1, 15] = Convert.ToString(this.courseDictionary[id].averageScore);
                r_2_1 = r_2_1 + 1;
            }
            //2.2 build table column Name
            string[] courseInfoTableColName = new string[16];
            courseInfoTableColName[0] = "id";
            courseInfoTableColName[1] = "CourseID";
            courseInfoTableColName[2] = "LectureID";
            courseInfoTableColName[3] = "TutorID";
            courseInfoTableColName[4] = "Core/Elective";
            courseInfoTableColName[15] = "Average Rating";
            for (int i = 0; i < this.courseDictionary[1].studentList.Count; i++)
                courseInfoTableColName[i + 5] = String.Format("Student_{0}",i+1);
            gvCourseInfo.DataSource = ConvertToDataTable(courseInfoTableColName, courseInforTable);
            gvCourseInfo.DataBind();

            //3. display student attribute weight
            //   build data array and table name
            string[,] stuAttrWeightTable = new string[1, tStudentSimilarityCalculation.stuAttributeWeightDic.Count+1];
            string[] stuAttrWeightTableColName = new string[tStudentSimilarityCalculation.stuAttributeWeightDic.Count+1];
            int c_7 = 0;
            foreach (string attrName in tStudentSimilarityCalculation.stuAttributeWeightDic.Keys)
            {
                stuAttrWeightTable[0, c_7] = Convert.ToString(String.Format("{0:0.####}",
                    tStudentSimilarityCalculation.stuAttributeWeightDic[attrName]));
                stuAttrWeightTableColName[c_7] = attrName;
                c_7 = c_7 + 1;
            }
            stuAttrWeightTableColName[tStudentSimilarityCalculation.stuAttributeWeightDic.Count] = "ThresHold";
            stuAttrWeightTable[0,tStudentSimilarityCalculation.stuAttributeWeightDic.Count] = Convert.ToString(String.Format("{0:0.####}",
                    tStudentSimilarityCalculation.thresHold)); 

            gvStuAttrWeight.DataSource = ConvertToDataTable(stuAttrWeightTableColName, stuAttrWeightTable);
            gvStuAttrWeight.DataBind();

            //4. display students information and rating courses order by student similarity score 
            //4.1 build student rating array
            //The array size is: 10 lines(each line represents one student) and studentID + Similarity Score + courseID
            string[,] studentSimilarityRatingTable=new string[10,this.courseDictionary.Count+2];
            int r_1 = 0;
            int c_1 = 0;
            foreach (int stuID in tStudentSimilarityCalculation.sortedSimilarityResult.Keys)
            {
                c_1 = 2;
                studentSimilarityRatingTable[r_1, 0] = Convert.ToString(stuID);
                studentSimilarityRatingTable[r_1, 1] = Convert.ToString(tStudentSimilarityCalculation.sortedSimilarityResult[stuID]);

                foreach (int courseID in this.studentDictionary[stuID].sortedCourseRatingDic.Keys)
                {
                    studentSimilarityRatingTable[r_1, c_1] = Convert.ToString(String.Format("{0}-{1}", 
                        courseID, this.studentDictionary[stuID].sortedCourseRatingDic[courseID]));
                    c_1 = c_1 + 1;
                }
                r_1 = r_1 + 1;
            }
            //4.2 build table column Name
            string[] stuTableColName = new string[this.courseDictionary.Count + 2];
            stuTableColName[0] = "StudentID";
            stuTableColName[1] = "Score";
            for (int i = 0; i < this.courseDictionary.Count;i++ )
            {
                stuTableColName[i + 2] = String.Format("Course_{0}",Convert.ToString(i+1));
            }
            gvStudentsRating.DataSource = ConvertToDataTable(stuTableColName,studentSimilarityRatingTable);
            gvStudentsRating.DataBind();

            //5. display combination course rating results
            //5.1 build combination course rating array
            string[,] coureCombinationRatingTable = new string[1, tCalculateCombinationSimilarity.sortedCourseSimiAverageDictionary.Count];
            int c_5 = 0;
            foreach (int courseID in tCalculateCombinationSimilarity.sortedCourseSimiAverageDictionary.Keys)
            {
                coureCombinationRatingTable[0, c_5] = Convert.ToString(String.Format("{0}-{1:0.####}",
                    courseID, tCalculateCombinationSimilarity.sortedCourseSimiAverageDictionary[courseID]));
                c_5 = c_5 + 1;
            }
            //5.2 build combination course rating table name
            string[] coureCombinationRatingTableColName = new string[tCalculateCombinationSimilarity.sortedCourseSimiAverageDictionary.Count];
            for (int i = 0; i < tCalculateCombinationSimilarity.sortedCourseSimiAverageDictionary.Count; i++)
            {
                coureCombinationRatingTableColName[i] = String.Format("Course_{0}", Convert.ToString(i + 1));
            }
            gvSumStudentCourseRating.DataSource = ConvertToDataTable(coureCombinationRatingTableColName, coureCombinationRatingTable);
            gvSumStudentCourseRating.DataBind();

            //when executeSlopeOneFlag dose not equal to 0 then display these two tables
            if (this.executeSlopeOneFlag != 0)
            {
                //6. display course similarity information
                //6.1 build table column Name
                string[] coureSimilarityTableColName = new string[coureSimilarityTable.GetLength(1)];
                coureSimilarityTableColName[0] = "CourseID";
                coureSimilarityTableColName[1] = "Most Similar CourseID";
                for (int i = 2; i < coureSimilarityTableColName.Length; i++)
                {
                    coureSimilarityTableColName[i] = String.Format("Course_{0}", Convert.ToString(i - 1));
                }
                gvCourseSimilarity.DataSource = ConvertToDataTable(coureSimilarityTableColName, coureSimilarityTable);
                gvCourseSimilarity.DataBind();

                //7. display sorted course prediction score 
                //7.1 build sorted predict array
                string[,] courePredictRatingTable = new string[1, tSlopeOneCalculation.sortedPredictCourseRatingDic.Count];
                int c_3 = 0;
                foreach (int courseID in tSlopeOneCalculation.sortedPredictCourseRatingDic.Keys)
                {
                    courePredictRatingTable[0, c_3] = Convert.ToString(String.Format("{0}-{1:0.####}",
                        courseID, tSlopeOneCalculation.sortedPredictCourseRatingDic[courseID]));
                    c_3 = c_3 + 1;
                }
                //7.2 build table column Name
                string[] courseTableColName = new string[tSlopeOneCalculation.sortedPredictCourseRatingDic.Count];
                for (int i = 0; i < tSlopeOneCalculation.sortedPredictCourseRatingDic.Count; i++)
                {
                    courseTableColName[i] = String.Format("Course_{0}", Convert.ToString(i + 1));
                }
                gvCoursePreRating.DataSource = ConvertToDataTable(courseTableColName, courePredictRatingTable);
                gvCoursePreRating.DataBind();
            }

            //8. display final combination course rating results
            //8.1 build final combination course rating array
            string[,] finalResultTable = new string[1, tCalculateCombinationSimilarity.sortedFinalResultDictionary.Count];
            int c_6 = 0;
            foreach (int courseID in tCalculateCombinationSimilarity.sortedFinalResultDictionary.Keys)
            {
                finalResultTable[0, c_6] = Convert.ToString(String.Format("{0}(id:{1})-{2:0.####}",
                    this.courseDictionary[courseID].courseID,courseID, tCalculateCombinationSimilarity.sortedFinalResultDictionary[courseID]));
                c_6 = c_6 + 1;
            }
            //8.2 build final combination course rating table name
            string[] finalResultTableColName = new string[tCalculateCombinationSimilarity.sortedFinalResultDictionary.Count];
            for (int i = 0; i < tCalculateCombinationSimilarity.sortedFinalResultDictionary.Count; i++)
            {
                finalResultTableColName[i] = String.Format("Course_{0}", Convert.ToString(i + 1));
            }
            gvFinalResult.DataSource = ConvertToDataTable(finalResultTableColName, finalResultTable);
            gvFinalResult.DataBind();
        }
        /**********************************************************************************************************************
        /***********************************        Function Definition Part     **********************************************
        /**********************************************************************************************************************
        /**
         * read student training data from uploading file
         */
        public Boolean readStudentsInfo()
        {
            try
            {
                StreamReader fileReader = new StreamReader(studentsInfoFilePath);
                string curLine = "";
                string[] curLineArray;
                Student tStudent;
                while ((curLine = fileReader.ReadLine()) != null)
                {
                    if (curLine.Contains("#"))
                        continue;
                    curLineArray = curLine.Split(',');
                    tStudent = new Student();
                    tStudent.id = Convert.ToInt32(curLineArray[0]);
                    tStudent.education = Convert.ToInt32(curLineArray[1]);
                    tStudent.sex = Convert.ToInt32(curLineArray[2]);
                    tStudent.type = Convert.ToInt32(curLineArray[3]);
                    tStudent.GPA = Convert.ToInt32(curLineArray[4]);
                    this.studentDictionary.Add(tStudent.id, tStudent);
                }
                fileReader.Close();
                return true;
            }
            catch(Exception ex)
            {
                lbMessage.Text = "ERROR: " + ex.Message.ToString() ;
                return false;
            }
        }
        /**
         * ead course training data from uploading file
         */
        public Boolean readCoursesInfo()
        {
            try
            {
                StreamReader fileReader = new StreamReader(coursesInfoFilePath);
                string curLine = "";
                string[] curLineArray;
                Course tCourse;
                int i = 1;
                int numOfRating = 0;
                int sum = 0;
                while ((curLine = fileReader.ReadLine()) != null)
                {
                    if (curLine.Contains("#"))
                        continue;
                    numOfRating = 0;
                    sum = 0;
                    curLineArray = curLine.Split(',');
                    tCourse = new Course();
                    tCourse.id = i;
                    tCourse.courseID = Convert.ToInt32(curLineArray[0]);
                    tCourse.lectureID = Convert.ToInt32(curLineArray[1]);
                    tCourse.tutorID = Convert.ToInt32(curLineArray[2]);
                    tCourse.type = Convert.ToInt32(curLineArray[3]);
                    tCourse.studentList.Add(curLineArray[4].Equals("") ? 0 : Convert.ToInt32(curLineArray[4]));
                    tCourse.studentList.Add(curLineArray[5].Equals("") ? 0 : Convert.ToInt32(curLineArray[5]));
                    tCourse.studentList.Add(curLineArray[6].Equals("") ? 0 : Convert.ToInt32(curLineArray[6]));
                    tCourse.studentList.Add(curLineArray[7].Equals("") ? 0 : Convert.ToInt32(curLineArray[7]));
                    tCourse.studentList.Add(curLineArray[8].Equals("") ? 0 : Convert.ToInt32(curLineArray[8]));
                    tCourse.studentList.Add(curLineArray[9].Equals("") ? 0 : Convert.ToInt32(curLineArray[9]));
                    tCourse.studentList.Add(curLineArray[10].Equals("") ? 0 : Convert.ToInt32(curLineArray[10]));
                    tCourse.studentList.Add(curLineArray[11].Equals("") ? 0 : Convert.ToInt32(curLineArray[11]));
                    tCourse.studentList.Add(curLineArray[12].Equals("") ? 0 : Convert.ToInt32(curLineArray[12]));
                    tCourse.studentList.Add(curLineArray[13].Equals("") ? 0 : Convert.ToInt32(curLineArray[13]));
                    //calculate average score
                    for (int j = 0; j < tCourse.studentList.Count; j++)
                    {
                        if (tCourse.studentList[j] != 0)
                        {
                            numOfRating = numOfRating + 1;
                            sum = sum + tCourse.studentList[j];
                        }
                    }
                    tCourse.averageScore = (sum * 1.0) / numOfRating;
                    this.courseDictionary.Add(tCourse.id, tCourse);
                    i = i + 1;
                }
                fileReader.Close();
                return true;
            }
            catch (Exception ex)
            {
                lbMessage.Text = "ERROR: " + ex.Message.ToString();
                return false;
            }
        }
        /**
         * Read student course rating information into student dictionary
         */
        public void readStudentSortedRatingForCourses()
        {
            //add course rating score for each student
            foreach (int courseID in this.courseDictionary.Keys)
            {
                for (int i = 0; i < this.courseDictionary[courseID].studentList.Count; i++)
                {
                    this.studentDictionary[i + 1].courseRatingDictionary.Add(courseID, this.courseDictionary[courseID].studentList[i]);
                }
            }

            //sort rating result by rating score
            foreach (int stuID in this.studentDictionary.Keys)
            {
                this.studentDictionary[stuID].sortedCourseRating();
            }
        }
        /**
         * read student information from Interface
         */
        public void readInputStudentInfo(Student tStudent)
        {
            //pg_ug: 0 means postgrad, 1 means undergrad
            if(rbPostgrad.Checked == true)
                tStudent.education = 0;
            else 
                tStudent.education = 1;
            //int_local: 0 means international, 1 means local
            if (rbInternational.Checked == true)
                tStudent.type = 0;
            else
                tStudent.type = 1;
            //m_f: O means male, 1 means female
            if (rbMale.Checked == true)
                tStudent.sex = 0;
            else
                tStudent.sex = 1;
            //GPA
            if (ddlGPA.SelectedValue.Equals(""))
                tStudent.GPA = 0;
            else
                tStudent.GPA = Convert.ToInt32(ddlGPA.SelectedValue);

            //tStudent.id = 11;
            //tStudent.education = 0;
            //tStudent.sex = 0;
            //tStudent.type = 0;
            //tStudent.GPA = 4;
        }
        /**
         * read course information from Interface 
         * and also find the most similar coure for specific course
         */
        public string[,] readInputCourseInfo(Student tStudent)
        {
            //count how many courses are rated from interface
            int select_Course_Count = 0;
            if (!ddlRate1.SelectedValue.Equals("NOT COMPLETED"))
            {
                select_Course_Count = select_Course_Count + 1;
                this.maxCourseID = 1;
                this.completedCoursesList.Add(1);
            }
            if (!ddlRate2.SelectedValue.Equals("NOT COMPLETED"))
            {
                select_Course_Count = select_Course_Count + 1;
                this.maxCourseID = 2;
                this.completedCoursesList.Add(2);
            }
            if (!ddlRate3.SelectedValue.Equals("NOT COMPLETED"))
            {
                select_Course_Count = select_Course_Count + 1;
                this.maxCourseID = 3;
                this.completedCoursesList.Add(3);
            }
            if (!ddlRate4.SelectedValue.Equals("NOT COMPLETED"))
            {
                select_Course_Count = select_Course_Count + 1;
                this.maxCourseID = 4;
                this.completedCoursesList.Add(4);
            }
            //no course was selected
            if (select_Course_Count == 0)
                return null;

            //initial display table
            string[,] courseSimilarityTable = new string[select_Course_Count, this.courseDictionary.Count + 2];
            int rowNum = 0;
            Course tCourse = null;
            if (!ddlRate1.SelectedValue.Equals("NOT COMPLETED"))
            {
                //get input from interface
                tCourse = new Course();
                tCourse.courseID = 1;
                tCourse.lectureID = Convert.ToInt32(tbLecturer1.Text);
                tCourse.tutorID = Convert.ToInt32(tbTutor1.Text);
                tCourse.averageScore = Convert.ToDouble(ddlRate1.SelectedValue);

                //tCourse = new Course();
                //tCourse.courseID = 1;
                //tCourse.lectureID = 2;
                //tCourse.tutorID = 1;
                //tCourse.averageScore = 4;

                //find the most similar course
                int tFlag = this.findMostSimilarCourse(tStudent, tCourse, courseSimilarityTable, rowNum);
                //find similar course
                if (tFlag != 0)
                    rowNum = rowNum + 1;
            }
            if (!ddlRate2.SelectedValue.Equals("NOT COMPLETED"))
            {
                //get input from interface
                tCourse = new Course();
                tCourse.courseID = 2;
                tCourse.lectureID = Convert.ToInt32(tbLecturer2.Text);
                tCourse.tutorID = Convert.ToInt32(tbTutor2.Text);
                tCourse.averageScore = Convert.ToDouble(ddlRate2.SelectedValue);

                //tCourse = new Course();
                //tCourse.courseID = 2;
                //tCourse.lectureID = 2;
                //tCourse.tutorID = 1;
                //tCourse.averageScore = 4;

                //find the most similar course
                int tFlag = this.findMostSimilarCourse(tStudent, tCourse, courseSimilarityTable, rowNum);
                //find similar course
                if (tFlag != 0)
                    rowNum = rowNum + 1;
            }
            if (!ddlRate3.SelectedValue.Equals("NOT COMPLETED"))
            {
                //get input from interface
                tCourse = new Course();
                tCourse.courseID = 3;
                tCourse.lectureID = Convert.ToInt32(tbLecturer3.Text);
                tCourse.tutorID = Convert.ToInt32(tbTutor3.Text);
                tCourse.averageScore = Convert.ToDouble(ddlRate3.SelectedValue);

                //tCourse = new Course();
                //tCourse.courseID = 3;
                //tCourse.lectureID = 2;
                //tCourse.tutorID = 2;
                //tCourse.averageScore = 3;

                //find the most similar course
                int tFlag = this.findMostSimilarCourse(tStudent, tCourse, courseSimilarityTable, rowNum);
                //find similar course
                if (tFlag != 0)
                    rowNum = rowNum + 1;
            }
            if (!ddlRate4.SelectedValue.Equals("NOT COMPLETED"))
            {
                //get input from interface
                tCourse = new Course();
                tCourse.courseID = 4;
                tCourse.lectureID = Convert.ToInt32(tbLecturer4.Text);
                tCourse.tutorID = Convert.ToInt32(tbTutor4.Text);
                tCourse.averageScore = Convert.ToDouble(ddlRate4.SelectedValue);

                //tCourse = new Course();
                //tCourse.courseID = 14;
                //tCourse.lectureID = 12;
                //tCourse.tutorID = 11;
                //tCourse.averageScore = 4;

                //find the most similar course
                int tFlag = this.findMostSimilarCourse(tStudent, tCourse, courseSimilarityTable, rowNum);
                //find similar course
                if (tFlag != 0)
                    rowNum = rowNum + 1;
            }
            //can not find any similar course for all the input courses
            if (rowNum == 0)
                return null;

            //if the number of similar founded courses less than select_Course_Count, have to rebuild courseSimilarityTable for display
            if (rowNum != select_Course_Count)
            {
                string[,] newCourseSimilarityTable = new string[rowNum, this.courseDictionary.Count + 2];
                for (int i = 0; i < rowNum; i++)
                    for (int j = 0; j < courseSimilarityTable.GetLength(1); j++)
                        newCourseSimilarityTable[i,j]=courseSimilarityTable[i,j];
                return newCourseSimilarityTable;
            }
            //
            return courseSimilarityTable;
        }
        /**
         * find the most similar course for a specific course
         */
        public int findMostSimilarCourse(Student tStudent, Course tCourse, string[,] courseSimilarityTable,int rowNum)
        {
            int cid = 0;
            CourseSimilarityCalculation_v2 tCourseSimilarityCalculation = new CourseSimilarityCalculation_v2();
            cid = tCourseSimilarityCalculation.calculateSimilarity(tCourse, this.courseDictionary);
            //if the algorithm cannot find any similar course(return value is 0)
            if (cid == 0)
                return 0;
            //the algorithm can find similar course
            else
            {
                tStudent.courseRatingDictionary.Add(cid, Convert.ToInt32(tCourse.averageScore));
                //add value to display table
                courseSimilarityTable[rowNum, 0] = Convert.ToString(tCourse.courseID);
                courseSimilarityTable[rowNum, 1] = Convert.ToString(cid);
                int c_2 = 2;
                foreach (int courseID in tCourseSimilarityCalculation.sortedSimilarityResult.Keys)
                {
                    courseSimilarityTable[rowNum, c_2] = Convert.ToString(String.Format("{0}-{1:0.####}-{2:0.####}",
                        courseID, tCourseSimilarityCalculation.sortedSimilarityResult[courseID], this.courseDictionary[courseID].averageScore));
                    c_2 = c_2 + 1;
                }
            }
            //
            return 1;
        }
        /**
         * This function use for generate DataTable DataSource for displaying GraidView 
         */
        private DataTable ConvertToDataTable(string[] colNameArray, string[,] dataArray)
        {
            DataTable dataSouce = new DataTable();
            //for (int i = 0; i < dataArray.GetLength(1); i++)
            //{
            //    DataColumn newColumn = new DataColumn(i.ToString(), Type.GetType("System.String"));
            //    dataSouce.Columns.Add(newColumn);
            //}
            for (int i = 0; i < colNameArray.Length; i++)
            {
                DataColumn newColumn = new DataColumn(colNameArray[i], Type.GetType("System.String"));
                dataSouce.Columns.Add(newColumn);
            }
            for (int i = 0; i < dataArray.GetLength(0); i++)
            {
                DataRow newRow = dataSouce.NewRow();
                for (int j = 0; j < dataArray.GetLength(1); j++)
                {
                    //newRow[j.ToString()] = dataArray[i, j];
                    newRow[colNameArray[j]] = dataArray[i, j];
                }
                dataSouce.Rows.Add(newRow);
            }
            return dataSouce;
        }
        /*
         * upload test csv files
         */
        protected void Button2_Click(object sender, EventArgs e)
        {
            if(FileUpload1.HasFile)
            {
                try {
                    FileUpload1.SaveAs(tbFileUploadPath.Text+"\\"+FileUpload1.FileName);
                    //FileUpload1.SaveAs("C:/Users/LL/Desktop/"+FileUpload1.FileName);
                    lbMessageForUpload.Text = "Upload file successful!";
                }
                catch(Exception ex){
                    lbMessageForUpload.Text = "ERROR: " + ex.Message.ToString();
                }
            }
            else
            {
                lbMessageForUpload.Text = "You have not specified a file!";
            }
        }
    }
}