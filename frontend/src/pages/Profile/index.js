import React, { useEffect, useState } from 'react';
import restapi from 'src/api/restapi';
import ButtonComp from 'src/components/ButtonComp/ButtonComp';
import Imgbox from 'src/components/Imgcomponents/Imgbox';
import Navbar from 'src/components/Navbar/Navbar';
import styles from './index.module.css';


const Profile = (props) => {
  const [userInfo, setUserInfo] = useState({});

  useEffect(() => {
    restapi.get(`/user/${props.match.params.id}`)
    .then((response) => {
      setUserInfo(response.data.data);
    })
    .catch(() => {
      props.history.push({pathname: '/home'})
    })
  }, [])

  const renderBtnComponent = () => {

    const userId = localStorage.getItem('no');

    return (
      props.match.params.id === userId ? null :
      <div className={styles.otherDisplayBtn}>
        <ButtonComp width="regular" type="base" textvalue="팔로우" className={styles.followBtn}/>
        <ButtonComp width="regular" type="base" textvalue="메시지" className={styles.messageBtn}/>
      </div>
    )
  }

  return (
    <>
      <Navbar color="white">
        <span onClick={() => {

          }}>
        <i className={"fas fa-chevron-left color_black " + styles.backIcon} style={{ cursor: "pointer" }}></i>
        </span>
        <h1 className={styles.userId}>{userInfo.nickname}</h1>
      </Navbar>
      
      <div className={styles.userBord}>
        <Imgbox src={userInfo.profileImageUrl} size="small" shape="circle" className={styles.userImg}/>
        <div className={styles.userInfo}>
          <div className={styles.userInfoItem}>
            <h2 className={styles.userInfoNum}>{userInfo.articleNum}</h2>
            <h3 className={styles.userInfoLet}>게시물</h3>
          </div>
          <div className={styles.userInfoItem}>
            <h2 className={styles.userInfoNum}>{userInfo.followerNum}</h2>
            <h3 className={styles.userInfoLet}>팔로워</h3>
          </div>
          <div>
            <h2 className={styles.userInfoNum}>{userInfo.followingNum}</h2>
            <h3 className={styles.userInfoLet}>팔로잉</h3>
          </div>
        </div>
      </div>
      <h1 className={styles.userNickname}>{userInfo.nickname}</h1>
      {renderBtnComponent()}
      <div className={styles.userIntro}>
        {userInfo.description}
      </div>
    </>
  );
};

export default Profile;