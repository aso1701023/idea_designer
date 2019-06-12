package jp.ac.asojuku.st.idea_designer

abstract class CloneInterface : Cloneable {
    abstract fun createClone() : CloneInterface
}