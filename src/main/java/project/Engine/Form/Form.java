package project.Engine.Form;

import java.util.*;

/**
 * This class represents a 'form' that users are required to fill in for the process of creating
 * a booking.
 */

public class Form {
    private List<Question> questions = new ArrayList<>();

    /**
     * Simulates an objective question:
     *  a question with multiple choices to choose from but with only one answer.
     *  More than one choice selected is not allowed.
     */
    public class Question {
        private String question;   // the question
        // a list of possible answers or options to choose from
        private List<String> options = new ArrayList<>();
        private int answerIndex = -1; // answer chosen as an index relative to options list
        private String answer;

        public Question(String question) {
            this.question = question;
        }

        /**
         * This method gets the question from the form
         *
         * @return Returns question
         */
        public String getQuestion() {
            return question;
        }

        /**
         * This method allows questions to have more than 1 answers (having more options)
         *
         * @param option Available answers for the question
         */
        public void addOption(String option) {
            if (options != null) {
                options.add(option);
            }
        }

        /**
         * This method gets the answer chosen for the question
         *
         * @return Returns answer of the question
         */
        public String getAnswer() {
            return answer;
        }

        /**
         * This method sets the answer for a question
         *
         * @param answer Answer for a specific question
         * @throws IllegalArgumentException
         */
        public void setAnswer(String answer) throws IllegalArgumentException {
            boolean match = false;
            for (int i = 0; i < options.size(); i++) {
                String option = options.get(i).trim();
                answer = answer.trim();
                if (option.equalsIgnoreCase(answer)) {
                    this.answer = answer;
                    this.answerIndex = i;
                    match = true;
                    break;
                }
            }

            if (!match) {
                throw new IllegalArgumentException("option selected not in choices");
            }
        }

        public List<String> getOptions() {
            return options;
        }

        @Override
        public String toString() {
            String result = "Question: " + question + "\n" + "Choices: ";
            for (int i = 0; i < options.size(); i++) {
                if (i != (options.size() - 1)) {
                    result += (options.get(i) + ", ");
                } else {
                    result += (options.get(i));
                }

            }
            result += "\n";
            return result;
        }
    }

    public Form() {
        // Currently, the system should include only a type of form which is used by the
        // healthcare workers or administrator upon on-site testing (subsystem 4)
        String question = "Have you experienced any severe symptoms in the last few days?";
        List<String> options = new ArrayList<>(Arrays.asList("Yes", "No"));
        addQuestion(question, options);

        String question2 = "Recommended covid test type by doctor";
        List<String> options2 = new ArrayList<>(Arrays.asList("PCR", "RAT"));
        addQuestion(question2, options2);
    }

    /**
     * This method allows us to add additional questions to the form if necessary
     *
     * @param question Question to be added
     * @param options Possible answers for the question added
     */
    private void addQuestion(String question, List<String> options) {
        // creates the question
        Question newQuestion = new Question(question);

        // possible answers for the question
        for (String option : options) {
            newQuestion.addOption(option);
        }
        this.questions.add(newQuestion);
    }

    /**
     * This method sets the answer for a question
     *
     * @param answers Answer for a specific question
     * @throws IllegalArgumentException
     */
    public void setAnswers(List<String> answers) throws IllegalArgumentException {
        if (answers.size() != questions.size()) {
            throw new IllegalArgumentException(
                    "Number of answers provided doesn't match number of questions"
            );
        }

        // answer for question[index] = answers[index]
        for (int ind = 0; ind < answers.size(); ind++) {
            questions.get(ind).setAnswer(answers.get(ind));
        }
    }

    /**
     * This method get the answer of the question and maps it into a hashmap where the key
     * is the question and the value is the answer.
     *
     * @return Returns hashmap that contains the question and its answer
     */
    public Map<String, String> getQuestionAnswer() {
        Map<String, String> qna = new HashMap<>();
        for (Question question : questions) {
            // map to hashMap where (question, answer)
            qna.put(question.getQuestion(), question.getAnswer());
        }
        return qna;
    }

    public Map<String, List<String>> getQuestionOptions() {
        Map<String, List<String>> questionsOptions = new HashMap<>();
        for (Question question : questions) {
            questionsOptions.put(question.getQuestion(), question.options);
        }
        return questionsOptions;
    }

    public Question getQuestion(int index) {
        return questions.get(index);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public String getAnswer(int index) {
        return questions.get(index).getAnswer();
    }

    public int getNumberOfQuestions() {
        return questions.size();
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < questions.size(); i++) {
            result += questions.get(i)
                    .toString()
                    .replace("Question:", String.format("Question %d:", (i + 1)));
        }
        return result;
    }
}
