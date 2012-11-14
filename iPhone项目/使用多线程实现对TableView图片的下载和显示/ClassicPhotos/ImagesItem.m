//
//  ImagesItem.m
//  TestApp_1
//
//  Created by l l on 13/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import "ImagesItem.h"
#import <QuartzCore/QuartzCore.h>

@implementation ImagesItem
@synthesize imageArray = _imageArray , pc = _pc;

-(id)init
{
    self = [super init];
    if(self)
    {
        self.imageArray = [[NSMutableArray alloc] init];
        self.pc = [[UIPageControl alloc] init];
    }
    return self;
}

- (UIView *)viewForItem
{
    if (self.view == nil)
    {
        //Create ScrollView
        CGRect screenRect = [[UIScreen mainScreen] bounds];
        //        CGRect tScrollViewRect = CGRectMake(0, 0, screenRect.size.width-6, screenRect.size.height/3);
        CGRect tScrollViewRect = CGRectMake(0, 0, screenRect.size.width, screenRect.size.height/3);
        UIScrollView *scrollView = [[UIScrollView alloc] initWithFrame:tScrollViewRect];
        scrollView.backgroundColor = [UIColor clearColor];
        scrollView.delegate = self;
        scrollView.showsHorizontalScrollIndicator = NO;
        
        CGPoint curPoint = CGPointMake(-3, 6);
        CGRect imageViewRect = CGRectMake(curPoint.x, curPoint.y, screenRect.size.width, screenRect.size.height/3-12);
        UIImageView *imageView;
        
        for (int i=0;i<[self.imageArray count];i++) {
            imageView = [[UIImageView alloc] initWithFrame:imageViewRect];
            //add border
            [imageView.layer setBorderColor: [[UIColor whiteColor] CGColor]];
            [imageView.layer setBorderWidth: 6.0];
            
            [imageView setImage:[UIImage imageNamed:[self.imageArray objectAtIndex:i]]];
            [scrollView addSubview:imageView];
            
            imageViewRect.origin.x += imageViewRect.size.width;
        }
        
        //Add content View to Scroll View
        [scrollView setPagingEnabled:YES];
        [scrollView setContentSize:CGSizeMake(imageViewRect.origin.x+3 , screenRect.size.height/3)];
        
        //Create the base view
        CGRect baseViewRect = CGRectMake(0, 0, screenRect.size.width, screenRect.size.height/3+10);
        UIView *baseView = [[UIView alloc] initWithFrame:baseViewRect];
        baseView.backgroundColor = [UIColor redColor];
        
        //Create the UIPageView
        self.pc.center = CGPointMake(baseViewRect.size.width/2, baseViewRect.size.height-7);
        [self.pc setNumberOfPages:[self.imageArray count]];
        self.pc.currentPage = 0;
        
        //add different views
        [baseView addSubview:scrollView];
        [baseView addSubview:self.pc];
        
        //return view
        self.view = baseView;
    }
    return self.view;
}

-(void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    CGPoint newPoint = scrollView.contentOffset;
//    NSLog(@"Current Point...%f",fabs(newPoint.x));
    self.pc.currentPage = newPoint.x/320;
}
    

/*
- (UIView *)viewForItem
{
    if (self.view == nil)
    {
        //Create ScrollView
        CGRect screenRect = [[UIScreen mainScreen] bounds];
//        CGRect tScrollViewRect = CGRectMake(0, 0, screenRect.size.width-6, screenRect.size.height/3);
        CGRect tScrollViewRect = CGRectMake(0, 0, screenRect.size.width, screenRect.size.height/3);
        UIScrollView *scrollView = [[UIScrollView alloc] initWithFrame:tScrollViewRect];
        scrollView.backgroundColor = [UIColor redColor];
        
        //Create content View
        CGRect tViewRect = tScrollViewRect;
        tViewRect.size.width  *= [self.imageArray count];
        tViewRect.size.height -= 6.0;
        
//        UIView *tView = [[UIView alloc] initWithFrame:tViewRect];
//        tView.backgroundColor = [UIColor grayColor];
        
        
        
        CGPoint curPoint = CGPointMake(6, 6);
        CGRect imageViewRect = CGRectMake(curPoint.x, curPoint.y, screenRect.size.width-16, screenRect.size.height/3-12);
        UIImageView *imageView;
        
        for (int i=0;i<[self.imageArray count];i++) {
            imageView = [[UIImageView alloc] initWithFrame:imageViewRect];
            //add border
            [imageView.layer setBorderColor: [[UIColor whiteColor] CGColor]];
            [imageView.layer setBorderWidth: 6.0];
            
            [imageView setImage:[UIImage imageNamed:[self.imageArray objectAtIndex:i]]];
            [scrollView addSubview:imageView];
            
            imageViewRect.origin.x += imageViewRect.size.width+6;
        }
        
        //Add content View to Scroll View
        [scrollView setPagingEnabled:YES];
        [scrollView setContentSize:CGSizeMake(imageViewRect.origin.x , screenRect.size.height/3)];
//        [scrollView setContentSize:tViewRect.size];
        
        self.view = scrollView;
    }
    return self.view;
}
*/

//- (UIView *)viewForItem
//{
//    if (self.view == nil)
//    {
//        //Create ScrollView
//        CGRect screenRect = [[UIScreen mainScreen] bounds];
//        CGRect tScrollViewRect = CGRectMake(0, 0, screenRect.size.width-6, screenRect.size.height/3);
//        UIScrollView *scrollView = [[UIScrollView alloc] initWithFrame:tScrollViewRect];
//        scrollView.backgroundColor = [UIColor redColor];
//        
//        //Create content View
//        CGRect tViewRect = tScrollViewRect;
//        tViewRect.size.width  *= [self.imageArray count];
//        tViewRect.size.height -= 6.0;
//        UIView *tView = [[UIView alloc] initWithFrame:tViewRect];
//        tView.backgroundColor = [UIColor grayColor];
//        
//        
//        
//        CGPoint curPoint = CGPointMake(3, 3);
//        CGRect imageViewRect = CGRectMake(curPoint.x, curPoint.y, tView.frame.size.width/[self.imageArray count]-16, tView.frame.size.height-6);
//        UIImageView *imageView;
//        
//        for (int i=0;i<[self.imageArray count];i++) {
//            imageView = [[UIImageView alloc] initWithFrame:imageViewRect];
//            [imageView setImage:[UIImage imageNamed:[self.imageArray objectAtIndex:i]]];
//            [tView addSubview:imageView];
//        
//            imageViewRect.origin.x += imageViewRect.size.width+6;
//        }
//        
//        //Add content View to Scroll View
//        [scrollView addSubview:tView];
//        [scrollView setPagingEnabled:YES];
//        [scrollView setContentSize:tViewRect.size];
//
//        self.view = scrollView;
//    }
//    return self.view;
//}

//- (UIView *)viewForItem
//{
//    if (self.view == nil)
//    {
//        CGRect screenRect = [[UIScreen mainScreen] bounds];
//        CGRect tViewRect = CGRectMake(0, 0, screenRect.size.width-6, screenRect.size.height/3);
//        UIView *tView = [[UIView alloc] initWithFrame:tViewRect];
//        tView.backgroundColor = [UIColor redColor];
//        
//        UIScrollView *scrollView = [[UIScrollView alloc] initWithFrame:<#(CGRect)#>];
//        
//        CGRect imageViewRect = CGRectMake(3, 3, tView.frame.size.width-6, screenRect.size.height/3-6);
//        UIImageView *imageView = [[UIImageView alloc] initWithFrame:imageViewRect];
//        
//        [imageView setImage:[UIImage imageNamed:@"image_1.JPG"]];
//        
//        [tView addSubview:imageView];
//        
//        self.view = tView;
//    }
//    return self.view;
//}

@end
